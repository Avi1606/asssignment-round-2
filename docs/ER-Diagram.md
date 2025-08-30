# Entity Relationship Diagram - Leave Management System

## Database Schema Design

### Core Entities and Relationships

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   DEPARTMENTS   │         │    EMPLOYEES    │         │  LEAVE_REQUESTS │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ id (PK)         │◄────────│ department_id   │────────►│ id (PK)         │
│ name            │         │ id (PK)         │         │ employee_id (FK)│
│ description     │         │ name            │         │ start_date      │
│ is_active       │         │ email           │         │ end_date        │
│ created_at      │         │ joining_date    │         │ leave_type      │
│ updated_at      │         │ manager_id (FK) │◄─┐      │ reason          │
└─────────────────┘         │ is_active       │  │      │ status          │
                            │ annual_balance  │  │      │ days_requested  │
                            │ sick_balance    │  │      │ approved_by_id  │
                            │ casual_balance  │  │      │ approver_comment│
                            │ created_at      │  │      │ approved_at     │
                            │ updated_at      │  │      │ created_at      │
                            └─────────────────┘  │      │ updated_at      │
                                      │          │      └─────────────────┘
                                      │          │               │
                                      └──────────┘               │
                                                                 │
                                                                 ▼
                            ┌─────────────────┐         ┌─────────────────┐
                            │ LEAVE_TRANSACTIONS        │
                            ├─────────────────┤         
                            │ id (PK)         │         
                            │ leave_request_id│◄────────┘
                            │ transaction_type│         
                            │ description     │         
                            │ balance_before  │         
                            │ balance_after   │         
                            │ days_affected   │         
                            │ performed_by_id │         
                            │ created_at      │         
                            └─────────────────┘         
```

## Table Definitions

### DEPARTMENTS
**Purpose**: Organizational structure for grouping employees
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Unique Constraints**: `name`
- **Indexes**: `name`

### EMPLOYEES
**Purpose**: Core employee information and leave balances
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**: 
  - `department_id` → DEPARTMENTS(id)
  - `manager_id` → EMPLOYEES(id) [Self-referential]
- **Unique Constraints**: `email`
- **Indexes**: `email`, `department_id`

### LEAVE_REQUESTS
**Purpose**: Leave applications and approval workflow
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**:
  - `employee_id` → EMPLOYEES(id)
  - `approved_by_id` → EMPLOYEES(id)
- **Indexes**: `employee_id`, `status`, `start_date, end_date`

### LEAVE_TRANSACTIONS
**Purpose**: Audit trail for all leave-related activities
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**:
  - `leave_request_id` → LEAVE_REQUESTS(id)
  - `performed_by_id` → EMPLOYEES(id)
- **Indexes**: `leave_request_id`, `transaction_type`, `created_at`

## Key Relationships

1. **Department → Employee (One-to-Many)**
   - One department can have multiple employees
   - Each employee belongs to exactly one department

2. **Employee → Employee (Self-Referential One-to-Many)**
   - Manager-subordinate relationship
   - One manager can have multiple subordinates
   - Each employee can have at most one manager

3. **Employee → Leave Request (One-to-Many)**
   - One employee can have multiple leave requests
   - Each leave request belongs to exactly one employee

4. **Employee → Leave Request (Approver Relationship)**
   - One employee (manager) can approve multiple leave requests
   - Each approved leave request has exactly one approver

5. **Leave Request → Leave Transaction (One-to-Many)**
   - One leave request can generate multiple transaction records
   - Each transaction is linked to exactly one leave request

## Business Rules Enforced by Schema

1. **Data Integrity**:
   - Email uniqueness across all employees
   - Department name uniqueness
   - Valid foreign key relationships

2. **Leave Management**:
   - Leave requests must have valid employee and date ranges
   - Approval requires valid manager relationship
   - Transaction log maintains complete audit trail

3. **Organizational Structure**:
   - Employees must belong to active departments
   - Manager hierarchy prevents circular references
   - Soft delete preserves historical data

4. **Balance Tracking**:
   - Leave balances stored directly on employee record
   - Transaction log tracks all balance changes
   - Support for multiple leave types (Annual, Sick, Casual)

## Performance Considerations

1. **Indexing Strategy**:
   - Primary indexes on all foreign keys
   - Composite index on date ranges for leave overlap checking
   - Email index for fast employee lookups

2. **Query Optimization**:
   - Eager loading for frequently accessed relationships
   - Pagination support for large datasets
   - Efficient joins for complex reporting queries

3. **Audit Performance**:
   - Separate transaction table prevents bloating main tables
   - Time-based indexing for efficient audit queries
   - Configurable retention policies for old transactions