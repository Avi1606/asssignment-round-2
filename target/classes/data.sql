-- Sample data for Leave Management System
-- This file populates the database with sample departments, employees, and leave requests

-- Insert Departments
INSERT INTO departments (name, description, is_active, created_at, updated_at) VALUES
('Engineering', 'Software Development and Engineering teams', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Human Resources', 'HR operations and people management', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Marketing', 'Marketing and brand management', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Finance', 'Financial operations and accounting', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Operations', 'Business operations and support', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Employees (Managers first)
INSERT INTO employees (name, email, joining_date, is_active, annual_leave_balance, sick_leave_balance, casual_leave_balance, department_id, manager_id, created_at, updated_at) VALUES
-- Engineering Team
('Avi Patel', 'avi.patel@company.com', '2023-01-15', true, 18, 10, 5, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Rahul Sharma', 'rahul.sharma@company.com', '2023-02-01', true, 21, 8, 7, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Priya Singh', 'priya.singh@company.com', '2023-03-10', true, 19, 10, 6, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Amit Kumar', 'amit.kumar@company.com', '2023-04-05', true, 21, 9, 7, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- HR Team
('Sneha Gupta', 'sneha.gupta@company.com', '2022-12-01', true, 15, 8, 4, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vikash Yadav', 'vikash.yadav@company.com', '2023-05-15', true, 20, 10, 7, 2, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Marketing Team  
('Anita Verma', 'anita.verma@company.com', '2023-01-20', true, 17, 9, 6, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Rajesh Bhatt', 'rajesh.bhatt@company.com', '2023-06-01', true, 21, 10, 7, 3, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Finance Team
('Deepak Agarwal', 'deepak.agarwal@company.com', '2022-11-15', true, 16, 7, 5, 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kavya Nair', 'kavya.nair@company.com', '2023-07-10', true, 21, 10, 7, 4, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Operations Team
('Suresh Reddy', 'suresh.reddy@company.com', '2023-02-15', true, 19, 8, 6, 5, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Meera Shah', 'meera.shah@company.com', '2023-08-01', true, 21, 10, 7, 5, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Sample Leave Requests
INSERT INTO leave_requests (start_date, end_date, leave_type, reason, status, days_requested, employee_id, created_at, updated_at) VALUES
-- Pending requests
('2024-01-15', '2024-01-19', 'ANNUAL', 'Family vacation planned for new year', 'PENDING', 5, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2024-01-22', '2024-01-24', 'SICK', 'Doctor appointment and recovery', 'PENDING', 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2024-02-05', '2024-02-09', 'ANNUAL', 'Wedding ceremony to attend', 'PENDING', 5, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2024-01-18', '2024-01-18', 'CASUAL', 'Personal work', 'PENDING', 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Approved requests
('2023-12-20', '2023-12-22', 'ANNUAL', 'Year end vacation', 'APPROVED', 3, 2, DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -8, CURRENT_TIMESTAMP)),
('2023-12-15', '2023-12-15', 'SICK', 'Fever and cold', 'APPROVED', 1, 3, DATEADD('DAY', -15, CURRENT_TIMESTAMP), DATEADD('DAY', -14, CURRENT_TIMESTAMP)),

-- Rejected requests  
('2023-12-25', '2023-12-29', 'ANNUAL', 'Christmas vacation', 'REJECTED', 5, 4, DATEADD('DAY', -20, CURRENT_TIMESTAMP), DATEADD('DAY', -18, CURRENT_TIMESTAMP));

-- Update approved/rejected requests with approver information
UPDATE leave_requests SET approved_by_id = 1, approved_at = updated_at, approver_comments = 'Approved for year-end break' WHERE status = 'APPROVED' AND employee_id = 2;
UPDATE leave_requests SET approved_by_id = 1, approved_at = updated_at, approver_comments = 'Get well soon' WHERE status = 'APPROVED' AND employee_id = 3;
UPDATE leave_requests SET approved_by_id = 1, approved_at = updated_at, approver_comments = 'Project deadline conflict' WHERE status = 'REJECTED' AND employee_id = 4;

-- Insert sample leave transactions (audit trail)
INSERT INTO leave_transactions (transaction_type, description, leave_request_id, performed_by_id, created_at) VALUES
('LEAVE_APPLIED', 'Leave application submitted for 5 days', 1, 2, CURRENT_TIMESTAMP),
('LEAVE_APPLIED', 'Leave application submitted for 3 days', 2, 3, CURRENT_TIMESTAMP),
('LEAVE_APPLIED', 'Leave application submitted for 5 days', 3, 4, CURRENT_TIMESTAMP),
('LEAVE_APPLIED', 'Leave application submitted for 1 day', 4, 6, CURRENT_TIMESTAMP),
('LEAVE_APPLIED', 'Leave application submitted for 3 days', 5, 2, DATEADD('DAY', -10, CURRENT_TIMESTAMP)),
('LEAVE_APPROVED', 'Leave request approved by Avi Patel: Approved for year-end break', 5, 1, DATEADD('DAY', -8, CURRENT_TIMESTAMP)),
('LEAVE_APPLIED', 'Leave application submitted for 1 day', 6, 3, DATEADD('DAY', -15, CURRENT_TIMESTAMP)),
('LEAVE_APPROVED', 'Leave request approved by Avi Patel: Get well soon', 6, 1, DATEADD('DAY', -14, CURRENT_TIMESTAMP)),
('LEAVE_APPLIED', 'Leave application submitted for 5 days', 7, 4, DATEADD('DAY', -20, CURRENT_TIMESTAMP)),
('LEAVE_REJECTED', 'Leave request rejected by Avi Patel: Project deadline conflict', 7, 1, DATEADD('DAY', -18, CURRENT_TIMESTAMP));