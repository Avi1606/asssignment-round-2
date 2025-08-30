# Database Design - Leave Management System

## Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│   Department    │──────▶│    Employee     │──────▶│  LeaveRequest   │
└─────────────────┘   1:n └─────────────────┘   1:n └─────────────────┘
│                           │                           │
│ - id (PK)                │ - id (PK)                │ - id (PK)
│ - name                   │ - employee_id            │ - employee_id (FK)
│ - description            │ - first_name             │ - leave_type
│ - created_at             │ - last_name              │ - start_date
│ - updated_at             │ - email                  │ - end_date
└─                         │ - phone_number           │ - total_days
                           │ - joining_date           │ - reason
                           │ - department_id (FK)     │ - status
                           │ - manager_id (FK)        │ - approved_by (FK)
                           │ - role                   │ - approval_date
                           │ - annual_leave_balance   │ - approval_comments
                           │ - sick_leave_balance     │ - created_at
                           │ - personal_leave_balance │ - updated_at
                           │ - active                 └─
                           │ - created_at
                           │ - updated_at
                           └─
                              │
                              │ 1:n
                              ▼
                        ┌─────────────────┐
                        │ LeaveTransaction│
                        └─────────────────┘
                        │
                        │ - id (PK)
                        │ - leave_request_id (FK)
                        │ - performed_by_id (FK)
                        │ - transaction_type
                        │ - old_status
                        │ - new_status
                        │ - old_balance
                        │ - new_balance
                        │ - days_adjusted
                        │ - comments
                        │ - transaction_date
                        └─
```

## Database Schema

### Tables

#### 1. departments
- **Purpose**: Store organizational departments
- **Primary Key**: id
- **Unique Constraints**: name

#### 2. employees
- **Purpose**: Store employee information and leave balances
- **Primary Key**: id
- **Foreign Keys**: 
  - department_id → departments(id)
  - manager_id → employees(id) [Self-referencing]
- **Unique Constraints**: employee_id, email
- **Indexes**: 
  - idx_employee_department (department_id)
  - idx_employee_manager (manager_id)
  - idx_employee_active (active)

#### 3. leave_requests
- **Purpose**: Store leave applications and their status
- **Primary Key**: id
- **Foreign Keys**: 
  - employee_id → employees(id)
  - approved_by → employees(id)
- **Indexes**:
  - idx_leave_employee (employee_id)
  - idx_leave_status (status)
  - idx_leave_dates (start_date, end_date)
  - idx_leave_approved_by (approved_by)

#### 4. leave_transactions
- **Purpose**: Audit trail for all leave-related activities
- **Primary Key**: id
- **Foreign Keys**: 
  - leave_request_id → leave_requests(id)
  - performed_by_id → employees(id)
- **Indexes**:
  - idx_transaction_leave_request (leave_request_id)
  - idx_transaction_performed_by (performed_by_id)
  - idx_transaction_date (transaction_date)

## Key Relationships

1. **Department → Employee**: One-to-Many
   - Each department can have multiple employees
   - Each employee belongs to one department

2. **Employee → Employee** (Manager): One-to-Many (Self-referencing)
   - Each manager can have multiple subordinates
   - Each employee can have one manager (optional)

3. **Employee → LeaveRequest**: One-to-Many
   - Each employee can have multiple leave requests
   - Each leave request belongs to one employee

4. **Employee → LeaveRequest** (Approver): One-to-Many
   - Each approver can approve multiple leave requests
   - Each leave request can have one approver (optional)

5. **LeaveRequest → LeaveTransaction**: One-to-Many
   - Each leave request can have multiple transactions (audit trail)
   - Each transaction belongs to one leave request

## Data Integrity Rules

1. **Referential Integrity**: All foreign key relationships are enforced
2. **Business Rules**: 
   - Employee cannot be their own manager
   - Leave request end date must be >= start date
   - Leave balances cannot be negative
   - Only active employees can apply for leave
3. **Audit Trail**: All leave operations are logged in leave_transactions

## Performance Considerations

1. **Indexing Strategy**:
   - Primary keys automatically indexed
   - Foreign keys indexed for join performance
   - Status and date fields indexed for filtering
   - Active flag indexed for employee lookups

2. **Query Optimization**:
   - Use of lazy loading for JPA relationships
   - Pagination support for large datasets
   - Optimized queries for common operations

3. **Data Archiving**:
   - Soft delete for employees (active flag)
   - Historical data retention for audit compliance
   - Partition strategy for transaction tables (future enhancement)