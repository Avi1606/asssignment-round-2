-- Sample data for Leave Management System

-- Insert departments
INSERT INTO departments (name, description, created_at, updated_at) VALUES
('Engineering', 'Software Development and Engineering', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Human Resources', 'HR Operations and Employee Management', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Marketing', 'Marketing and Brand Management', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sales', 'Sales and Business Development', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Finance', 'Finance and Accounting', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert employees (managers first, then subordinates)
INSERT INTO employees (employee_id, first_name, last_name, email, joining_date, department_id, annual_leave_balance, sick_leave_balance, used_annual_leave, used_sick_leave, status, created_at, updated_at) VALUES
('EMP001', 'John', 'Smith', 'john.smith@company.com', '2020-01-15', 1, 25, 12, 5, 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP002', 'Sarah', 'Johnson', 'sarah.johnson@company.com', '2019-03-20', 2, 25, 12, 8, 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP003', 'Michael', 'Brown', 'michael.brown@company.com', '2021-06-10', 1, 20, 10, 3, 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP004', 'Emily', 'Davis', 'emily.davis@company.com', '2020-09-05', 3, 22, 10, 7, 3, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP005', 'Robert', 'Wilson', 'robert.wilson@company.com', '2018-12-01', 4, 25, 12, 12, 4, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP006', 'Lisa', 'Anderson', 'lisa.anderson@company.com', '2022-02-14', 5, 20, 10, 2, 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP007', 'David', 'Martinez', 'david.martinez@company.com', '2021-11-20', 1, 20, 10, 6, 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EMP008', 'Jennifer', 'Garcia', 'jennifer.garcia@company.com', '2020-04-18', 2, 25, 12, 4, 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update employees to set manager relationships
UPDATE employees SET manager_id = 1 WHERE id IN (3, 7); -- John Smith manages Michael Brown and David Martinez
UPDATE employees SET manager_id = 2 WHERE id = 8; -- Sarah Johnson manages Jennifer Garcia
UPDATE employees SET manager_id = 5 WHERE id = 4; -- Robert Wilson manages Emily Davis

-- Insert some sample leave requests
INSERT INTO leave_requests (employee_id, start_date, end_date, leave_type, reason, status, days_requested, is_emergency, created_at, updated_at) VALUES
(3, '2024-01-15', '2024-01-19', 'ANNUAL', 'Family vacation', 'APPROVED', 5, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '2024-02-10', '2024-02-12', 'SICK', 'Medical appointment', 'APPROVED', 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '2024-03-01', '2024-03-01', 'PERSONAL', 'Personal matter', 'PENDING', 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '2024-02-28', '2024-03-01', 'ANNUAL', 'Weekend extension', 'REJECTED', 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert leave transactions for audit trail
INSERT INTO leave_transactions (leave_request_id, employee_id, performed_by_id, transaction_type, comments, leave_days_affected, created_at) VALUES
(1, 3, 3, 'CREATED', 'Leave request created', 5, CURRENT_TIMESTAMP),
(1, 3, 1, 'APPROVED', 'Approved by manager', 5, CURRENT_TIMESTAMP),
(2, 4, 4, 'CREATED', 'Leave request created', 3, CURRENT_TIMESTAMP),
(2, 4, 5, 'APPROVED', 'Approved for medical reasons', 3, CURRENT_TIMESTAMP),
(3, 7, 7, 'CREATED', 'Leave request created', 1, CURRENT_TIMESTAMP),
(4, 8, 8, 'CREATED', 'Leave request created', 2, CURRENT_TIMESTAMP),
(4, 8, 2, 'REJECTED', 'Insufficient notice period', 2, CURRENT_TIMESTAMP);