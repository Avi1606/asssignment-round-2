# API Documentation - Leave Management System

## Overview
This document provides comprehensive API documentation for the Leave Management System. The system provides RESTful APIs for managing employees, leave requests, and approvals with complete business logic validation.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the system operates without authentication for simplicity. In production, implement JWT or OAuth2 authentication.

---

## Employee Management APIs

### 1. Create Employee
**POST** `/employees`

Creates a new employee in the system.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@company.com", 
  "joiningDate": "2024-01-15",
  "departmentId": 1,
  "managerId": 2
}
```

**Response (201 Created):**
```json
{
  "id": 13,
  "name": "John Doe",
  "email": "john.doe@company.com",
  "joiningDate": "2024-01-15",
  "isActive": true,
  "annualLeaveBalance": 21,
  "sickLeaveBalance": 10,
  "casualLeaveBalance": 7,
  "totalLeaveBalance": 38,
  "department": {
    "id": 1,
    "name": "Engineering",
    "description": "Software Development and Engineering teams"
  },
  "manager": {
    "id": 2,
    "name": "Manager Name",
    "email": "manager@company.com"
  },
  "createdAt": "2024-01-01T10:30:00",
  "updatedAt": "2024-01-01T10:30:00"
}
```

### 2. Get All Employees
**GET** `/employees`

Retrieves all active employees with their details.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Avi Patel",
    "email": "avi.patel@company.com",
    "joiningDate": "2023-01-15",
    "isActive": true,
    "annualLeaveBalance": 18,
    "sickLeaveBalance": 10,
    "casualLeaveBalance": 5,
    "totalLeaveBalance": 33,
    "department": {
      "id": 1,
      "name": "Engineering",
      "description": "Software Development and Engineering teams"
    },
    "manager": null,
    "createdAt": "2023-12-01T10:00:00",
    "updatedAt": "2023-12-01T10:00:00"
  }
]
```

### 3. Get Employee by ID
**GET** `/employees/{id}`

Retrieves a specific employee by their ID.

**Path Parameters:**
- `id` (required): Employee ID

**Response (200 OK):** Same as employee object above

### 4. Update Employee
**PUT** `/employees/{id}`

Updates an existing employee's information.

**Path Parameters:**
- `id` (required): Employee ID

**Request Body:** Same as create employee

**Response (200 OK):** Updated employee object

### 5. Delete Employee
**DELETE** `/employees/{id}`

Deactivates an employee (soft delete).

**Path Parameters:**
- `id` (required): Employee ID

**Response (204 No Content)**

### 6. Get Employees by Department
**GET** `/employees/department/{departmentId}`

**Path Parameters:**
- `departmentId` (required): Department ID

### 7. Get Employees by Manager
**GET** `/employees/manager/{managerId}`

**Path Parameters:**
- `managerId` (required): Manager's employee ID

---

## Leave Management APIs

### 1. Apply for Leave
**POST** `/leaves`

Submits a new leave request with comprehensive validation.

**Request Body:**
```json
{
  "startDate": "2024-02-15",
  "endDate": "2024-02-17", 
  "leaveType": "ANNUAL",
  "reason": "Family vacation",
  "employeeId": 2
}
```

**Leave Types:**
- `ANNUAL` - Annual/vacation leave
- `SICK` - Sick leave
- `CASUAL` - Casual leave
- `MATERNITY` - Maternity leave
- `PATERNITY` - Paternity leave  
- `EMERGENCY` - Emergency leave

**Response (201 Created):**
```json
{
  "id": 10,
  "startDate": "2024-02-15",
  "endDate": "2024-02-17", 
  "leaveType": "ANNUAL",
  "reason": "Family vacation",
  "status": "PENDING",
  "daysRequested": 3,
  "approverComments": null,
  "approvedAt": null,
  "employee": {
    "id": 2,
    "name": "Rahul Sharma", 
    "email": "rahul.sharma@company.com"
  },
  "approvedBy": null,
  "createdAt": "2024-01-01T10:30:00",
  "updatedAt": "2024-01-01T10:30:00"
}
```

### 2. Get All Leave Requests
**GET** `/leaves`

Retrieves all leave requests in the system.

**Response (200 OK):** Array of leave request objects

### 3. Get Leave Request by ID
**GET** `/leaves/{id}`

**Path Parameters:**
- `id` (required): Leave request ID

### 4. Approve Leave Request
**PUT** `/leaves/{id}/approve`

Approves a pending leave request.

**Path Parameters:**
- `id` (required): Leave request ID

**Query Parameters:**
- `approverId` (required): ID of the approving manager
- `comments` (optional): Approval comments

**Example:**
```
PUT /leaves/5/approve?approverId=1&comments=Approved%20for%20vacation
```

**Response (200 OK):** Updated leave request with approved status

### 5. Reject Leave Request
**PUT** `/leaves/{id}/reject`

Rejects a pending leave request.

**Path Parameters:**
- `id` (required): Leave request ID

**Query Parameters:**
- `approverId` (required): ID of the rejecting manager
- `comments` (optional): Rejection comments

### 6. Get Employee's Leave Requests
**GET** `/leaves/employee/{employeeId}`

**Path Parameters:**
- `employeeId` (required): Employee ID

### 7. Get Pending Leaves for Manager
**GET** `/leaves/pending/manager/{managerId}`

Retrieves all pending leave requests that require approval from a specific manager.

**Path Parameters:**
- `managerId` (required): Manager's employee ID

---

## Business Rules & Validations

### Employee Validation
1. Email must be unique across all employees
2. Employee must belong to an active department
3. Manager (if assigned) must be an active employee
4. Employee cannot be their own manager

### Leave Request Validation
1. **Date Validations:**
   - Start date cannot be in the past
   - End date must be on or after start date
   - Maximum leave duration is 30 days

2. **Balance Validations:**
   - Must have sufficient balance for requested leave type
   - Balance is checked at application and approval time

3. **Overlap Prevention:**
   - No overlapping pending or approved leave requests
   - System checks date ranges for conflicts

4. **Business Rules:**
   - Sick leave: Can be applied same day, max 5 days in advance
   - Annual leave: Requires 7 days advance notice
   - Casual leave: Requires 1 day advance notice

### Approval Workflow
1. Only direct manager can approve/reject leave requests
2. Employee cannot approve their own leave
3. Approver must be an active employee
4. Balance is deducted only upon approval
5. Complete audit trail maintained via transactions

---

## Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Insufficient Annual Leave balance. Required: 5, Available: 3",
  "timestamp": "2024-01-01T10:30:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Employee not found with ID: 999",
  "timestamp": "2024-01-01T10:30:00"
}
```

### 422 Validation Error
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-01T10:30:00",
  "errors": {
    "email": "Email must be valid",
    "name": "Name is required"
  }
}
```

---

## Status Codes

- **200 OK** - Successful GET, PUT operations
- **201 Created** - Successful POST operations
- **204 No Content** - Successful DELETE operations
- **400 Bad Request** - Business rule violations, validation errors
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Unexpected system errors

---

## Interactive API Documentation

When the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

The Swagger UI provides an interactive interface to test all APIs with live data.