-- Initial data for Leave Management System
-- Insert sample departments
INSERT INTO departments (id, name, description, created_at, updated_at) VALUES 
(1, 'Engineering', 'Software development and technical operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Human Resources', 'HR operations, recruitment, and employee relations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Marketing', 'Marketing campaigns, branding, and customer outreach', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Finance', 'Financial planning, accounting, and budget management', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample employees
INSERT INTO employees (id, employee_id, first_name, last_name, email, phone_number, joining_date, department_id, manager_id, role, annual_leave_balance, sick_leave_balance, personal_leave_balance, active, created_at, updated_at) VALUES 
-- Engineering Department
(1, 'EMP001', 'John', 'Doe', 'john.doe@company.com', '1234567890', '2023-01-15', 1, NULL, 'MANAGER', 21, 10, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'EMP002', 'Jane', 'Smith', 'jane.smith@company.com', '1234567891', '2023-02-01', 1, 1, 'EMPLOYEE', 21, 10, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'EMP003', 'Bob', 'Johnson', 'bob.johnson@company.com', '1234567892', '2023-03-10', 1, 1, 'EMPLOYEE', 19, 8, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- HR Department
(4, 'EMP004', 'Alice', 'Brown', 'alice.brown@company.com', '1234567893', '2023-01-20', 2, NULL, 'HR_ADMIN', 21, 10, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'EMP005', 'Charlie', 'Wilson', 'charlie.wilson@company.com', '1234567894', '2023-04-05', 2, 4, 'EMPLOYEE', 20, 9, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Marketing Department
(6, 'EMP006', 'Diana', 'Davis', 'diana.davis@company.com', '1234567895', '2023-02-15', 3, NULL, 'MANAGER', 21, 10, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'EMP007', 'Eva', 'Miller', 'eva.miller@company.com', '1234567896', '2023-05-01', 3, 6, 'EMPLOYEE', 21, 10, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Finance Department
(8, 'EMP008', 'Frank', 'Garcia', 'frank.garcia@company.com', '1234567897', '2023-01-10', 4, NULL, 'MANAGER', 21, 10, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'EMP009', 'Grace', 'Martinez', 'grace.martinez@company.com', '1234567898', '2023-03-20', 4, 8, 'EMPLOYEE', 18, 7, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample leave requests
INSERT INTO leave_requests (id, employee_id, leave_type, start_date, end_date, total_days, reason, status, approved_by, approval_date, approval_comments, created_at, updated_at) VALUES 
-- Approved leaves
(1, 2, 'ANNUAL', '2024-01-15', '2024-01-17', 3, 'Family vacation', 'APPROVED', 1, '2024-01-10 10:30:00', 'Approved. Enjoy your vacation!', '2024-01-05 09:00:00', '2024-01-10 10:30:00'),
(2, 3, 'SICK', '2023-12-20', '2023-12-22', 3, 'Flu symptoms', 'APPROVED', 1, '2023-12-20 11:00:00', 'Take care and get well soon', '2023-12-20 08:00:00', '2023-12-20 11:00:00'),

-- Pending leaves
(3, 5, 'ANNUAL', '2024-02-10', '2024-02-14', 5, 'Wedding anniversary celebration', 'PENDING', NULL, NULL, NULL, '2024-01-25 14:30:00', '2024-01-25 14:30:00'),
(4, 7, 'PERSONAL', '2024-02-05', '2024-02-05', 1, 'Personal appointment', 'PENDING', NULL, NULL, NULL, '2024-01-30 16:45:00', '2024-01-30 16:45:00'),

-- Rejected leave
(5, 9, 'ANNUAL', '2024-01-20', '2024-01-25', 6, 'Vacation request', 'REJECTED', 8, '2024-01-18 13:15:00', 'Cannot approve during busy period', '2024-01-15 11:20:00', '2024-01-18 13:15:00');

-- Insert sample leave transactions for audit trail
INSERT INTO leave_transactions (id, leave_request_id, performed_by_id, transaction_type, old_status, new_status, old_balance, new_balance, days_adjusted, comments, transaction_date) VALUES 
-- Leave applications
(1, 1, 2, 'LEAVE_APPLIED', NULL, 'PENDING', NULL, NULL, NULL, 'Leave application submitted', '2024-01-05 09:00:00'),
(2, 2, 3, 'LEAVE_APPLIED', NULL, 'PENDING', NULL, NULL, NULL, 'Leave application submitted', '2023-12-20 08:00:00'),
(3, 3, 5, 'LEAVE_APPLIED', NULL, 'PENDING', NULL, NULL, NULL, 'Leave application submitted', '2024-01-25 14:30:00'),

-- Leave approvals
(4, 1, 1, 'LEAVE_APPROVED', 'PENDING', 'APPROVED', 21, 18, 3, 'Approved. Enjoy your vacation!', '2024-01-10 10:30:00'),
(5, 2, 1, 'LEAVE_APPROVED', 'PENDING', 'APPROVED', 10, 7, 3, 'Take care and get well soon', '2023-12-20 11:00:00'),

-- Leave rejection
(6, 5, 8, 'LEAVE_REJECTED', 'PENDING', 'REJECTED', NULL, NULL, NULL, 'Cannot approve during busy period', '2024-01-18 13:15:00');

