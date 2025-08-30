# API Contracts - Leave Management System

## Base URL
- Development: `http://localhost:8080/api/v1`
- Production: `https://your-domain.com/api/v1`

## Authentication
Currently using basic authentication. Future versions will implement JWT tokens.

## Common Response Format

### Success Response
```json
{
  "data": {...},
  "message": "Success",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/employees",
  "fieldErrors": {
    "email": "Please provide a valid email address",
    "firstName": "First name is required"
  }
}
```

## Department Management APIs

### Create Department
**POST** `/departments`

**Request:**
```json
{
  "name": "Engineering",
  "description": "Software development and technical operations"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Engineering",
  "description": "Software development and technical operations",
  "employeeCount": 0
}
```

### Get All Departments
**GET** `/departments`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Engineering",
    "description": "Software development and technical operations",
    "employeeCount": 5
  },
  {
    "id": 2,
    "name": "Human Resources",
    "description": "HR operations and employee relations",
    "employeeCount": 3
  }
]
```

### Update Department
**PUT** `/departments/{id}`

**Request:**
```json
{
  "name": "Software Engineering",
  "description": "Software development, QA, and DevOps operations"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Software Engineering",
  "description": "Software development, QA, and DevOps operations",
  "employeeCount": 5
}
```

## Employee Management APIs

### Create Employee
**POST** `/employees`

**Request:**
```json
{
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "1234567890",
  "joiningDate": "2024-01-15",
  "departmentId": 1,
  "managerId": 2,
  "role": "EMPLOYEE"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "1234567890",
  "joiningDate": "2024-01-15",
  "departmentId": 1,
  "departmentName": "Engineering",
  "managerId": 2,
  "managerName": "Jane Smith",
  "role": "EMPLOYEE",
  "annualLeaveBalance": 21,
  "sickLeaveBalance": 10,
  "personalLeaveBalance": 5,
  "active": true
}
```

### Get Employee by ID
**GET** `/employees/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phoneNumber": "1234567890",
  "joiningDate": "2024-01-15",
  "departmentId": 1,
  "departmentName": "Engineering",
  "managerId": 2,
  "managerName": "Jane Smith",
  "role": "EMPLOYEE",
  "annualLeaveBalance": 18,
  "sickLeaveBalance": 10,
  "personalLeaveBalance": 5,
  "active": true
}
```

### Update Employee Leave Balances
**PATCH** `/employees/{employeeId}/leave-balances`

**Query Parameters:**
- `annualLeave`: (optional) New annual leave balance
- `sickLeave`: (optional) New sick leave balance
- `personalLeave`: (optional) New personal leave balance

**Response:** `200 OK`
```json
{
  "id": 1,
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "annualLeaveBalance": 25,
  "sickLeaveBalance": 12,
  "personalLeaveBalance": 7,
  // ... other fields
}
```

## Leave Request Management APIs

### Apply for Leave
**POST** `/leave-requests`

**Request:**
```json
{
  "employeeId": 1,
  "leaveType": "ANNUAL",
  "startDate": "2024-02-15",
  "endDate": "2024-02-17",
  "reason": "Family vacation"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "employeeId": 1,
  "employeeName": "John Doe",
  "departmentName": "Engineering",
  "leaveType": "ANNUAL",
  "startDate": "2024-02-15",
  "endDate": "2024-02-17",
  "totalDays": 3,
  "reason": "Family vacation",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Process Leave Approval
**PATCH** `/leave-requests/approval`

**Request:**
```json
{
  "leaveRequestId": 1,
  "decision": "APPROVED",
  "approverId": 2,
  "comments": "Approved. Enjoy your vacation!"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "employeeId": 1,
  "employeeName": "John Doe",
  "departmentName": "Engineering",
  "leaveType": "ANNUAL",
  "startDate": "2024-02-15",
  "endDate": "2024-02-17",
  "totalDays": 3,
  "reason": "Family vacation",
  "status": "APPROVED",
  "approvedById": 2,
  "approvedByName": "Jane Smith",
  "approvalDate": "2024-01-16T14:20:00Z",
  "approvalComments": "Approved. Enjoy your vacation!",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-16T14:20:00Z"
}
```

### Cancel Leave Request
**PATCH** `/leave-requests/{leaveRequestId}/cancel`

**Query Parameters:**
- `employeeId`: ID of employee requesting cancellation
- `reason`: (optional) Reason for cancellation

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "CANCELLED",
  "approvalComments": "Cancelled due to work requirements",
  // ... other fields
}
```

### Get Leave Requests by Employee
**GET** `/leave-requests/employee/{employeeId}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "leaveType": "ANNUAL",
    "startDate": "2024-02-15",
    "endDate": "2024-02-17",
    "totalDays": 3,
    "status": "APPROVED",
    // ... other fields
  },
  {
    "id": 2,
    "employeeId": 1,
    "employeeName": "John Doe",
    "leaveType": "SICK",
    "startDate": "2024-01-20",
    "endDate": "2024-01-20",
    "totalDays": 1,
    "status": "PENDING",
    // ... other fields
  }
]
```

### Get Pending Requests for Manager
**GET** `/leave-requests/manager/{managerId}/pending`

**Response:** `200 OK`
```json
[
  {
    "id": 3,
    "employeeId": 5,
    "employeeName": "Alice Brown",
    "departmentName": "Engineering",
    "leaveType": "PERSONAL",
    "startDate": "2024-02-10",
    "endDate": "2024-02-10",
    "totalDays": 1,
    "reason": "Medical appointment",
    "status": "PENDING",
    "createdAt": "2024-01-18T09:15:00Z"
  }
]
```

### Get Approved Leaves in Date Range
**GET** `/leave-requests/approved?startDate=2024-02-01&endDate=2024-02-29`

**Query Parameters:**
- `startDate`: Start date (YYYY-MM-DD format)
- `endDate`: End date (YYYY-MM-DD format)

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "employeeName": "John Doe",
    "departmentName": "Engineering",
    "leaveType": "ANNUAL",
    "startDate": "2024-02-15",
    "endDate": "2024-02-17",
    "totalDays": 3,
    "status": "APPROVED"
  }
]
```

## Error Codes

### HTTP Status Codes
- `200`: Success
- `201`: Created
- `400`: Bad Request (validation errors)
- `404`: Not Found
- `409`: Conflict (business rule violations)
- `500`: Internal Server Error

### Business Error Codes
- `INSUFFICIENT_BALANCE`: Not enough leave balance
- `OVERLAPPING_LEAVES`: Leave dates conflict with existing requests
- `INVALID_NOTICE_PERIOD`: Insufficient advance notice
- `UNAUTHORIZED_APPROVAL`: User cannot approve this request
- `INVALID_STATUS_TRANSITION`: Cannot change status from current state

## Rate Limiting
- 100 requests per minute per IP address
- 1000 requests per hour per authenticated user

## Pagination
List endpoints support pagination with query parameters:
- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 20, max: 100)
- `sort`: Sort criteria (e.g., `createdAt,desc`)

Example: `/leave-requests?page=0&size=10&sort=createdAt,desc`