# Business Logic Implementation - Leave Management System

## Leave Approval Workflow

### 1. Leave Application Process

```pseudocode
FUNCTION applyForLeave(leaveRequest):
    // Step 1: Validate Employee
    employee = getEmployeeById(leaveRequest.employeeId)
    IF employee.active == false:
        THROW "Employee is inactive"
    
    // Step 2: Validate Date Constraints
    today = getCurrentDate()
    IF leaveRequest.startDate < today:
        THROW "Start date cannot be in the past"
    
    IF leaveRequest.endDate < leaveRequest.startDate:
        THROW "End date must be after start date"
    
    // Step 3: Validate Notice Period
    daysNotice = calculateDaysBetween(today, leaveRequest.startDate)
    
    SWITCH leaveRequest.leaveType:
        CASE "SICK":
            IF daysNotice < 1:
                THROW "Minimum 1-day notice required for sick leave"
        CASE "ANNUAL":
            IF daysNotice < 7:
                THROW "Minimum 7-day advance notice required for vacation leave"
        CASE "PERSONAL":
            IF daysNotice < 3:
                THROW "Minimum 3-day advance notice required for personal leave"
    
    // Step 4: Calculate Total Days
    totalDays = calculateBusinessDays(leaveRequest.startDate, leaveRequest.endDate)
    leaveRequest.totalDays = totalDays
    
    // Step 5: Validate Leave Balance
    availableBalance = getLeaveBalance(employee, leaveRequest.leaveType)
    IF totalDays > availableBalance:
        THROW "Insufficient leave balance. Available: " + availableBalance + ", Requested: " + totalDays
    
    // Step 6: Check for Overlapping Requests
    overlappingRequests = findOverlappingLeaves(employee.id, leaveRequest.startDate, leaveRequest.endDate)
    IF overlappingRequests.size > 0:
        THROW "You have overlapping leave requests for the selected dates"
    
    // Step 7: Auto-assign Approver
    leaveRequest.assignedApprover = determineApprover(employee)
    
    // Step 8: Save Request
    leaveRequest.status = "PENDING"
    leaveRequest.createdAt = getCurrentTimestamp()
    savedRequest = saveLeaveRequest(leaveRequest)
    
    // Step 9: Create Audit Trail
    auditTransaction = createTransaction(
        leaveRequestId: savedRequest.id,
        performedBy: employee.id,
        transactionType: "LEAVE_APPLIED",
        comments: "Leave application submitted"
    )
    
    // Step 10: Send Notifications
    sendNotificationToApprover(leaveRequest.assignedApprover, savedRequest)
    
    RETURN savedRequest
END FUNCTION
```

### 2. Leave Approval Process

```pseudocode
FUNCTION processLeaveApproval(approvalRequest):
    // Step 1: Validate Leave Request
    leaveRequest = getLeaveRequestById(approvalRequest.leaveRequestId)
    IF leaveRequest == null:
        THROW "Leave request not found"
    
    IF leaveRequest.status != "PENDING":
        THROW "Leave request is not in pending status"
    
    // Step 2: Validate Approver Authority
    approver = getEmployeeById(approvalRequest.approverId)
    
    // Rule: Manager cannot approve own leave
    IF leaveRequest.employee.id == approver.id:
        THROW "You cannot approve your own leave request"
    
    // Rule: Check approval authority
    hasAuthority = checkApprovalAuthority(leaveRequest, approver)
    IF hasAuthority == false:
        THROW "You don't have permission to approve this leave request"
    
    // Step 3: Process Decision
    oldStatus = leaveRequest.status
    currentTime = getCurrentTimestamp()
    
    IF approvalRequest.decision == "APPROVED":
        // Approve the leave
        leaveRequest.status = "APPROVED"
        leaveRequest.approvedBy = approver.id
        leaveRequest.approvalDate = currentTime
        leaveRequest.approvalComments = approvalRequest.comments
        
        // Deduct leave balance
        deductLeaveBalance(leaveRequest.employee, leaveRequest.leaveType, leaveRequest.totalDays)
        
        auditType = "LEAVE_APPROVED"
        
    ELSE IF approvalRequest.decision == "REJECTED":
        // Reject the leave
        leaveRequest.status = "REJECTED"
        leaveRequest.approvedBy = approver.id
        leaveRequest.approvalDate = currentTime
        leaveRequest.approvalComments = approvalRequest.comments
        
        auditType = "LEAVE_REJECTED"
        
    ELSE:
        THROW "Invalid decision. Must be APPROVED or REJECTED"
    
    // Step 4: Save Updated Request
    updatedRequest = saveLeaveRequest(leaveRequest)
    
    // Step 5: Create Audit Trail
    auditTransaction = createTransaction(
        leaveRequestId: updatedRequest.id,
        performedBy: approver.id,
        transactionType: auditType,
        oldStatus: oldStatus,
        newStatus: leaveRequest.status,
        comments: approvalRequest.comments
    )
    
    // Step 6: Send Notifications
    sendNotificationToEmployee(leaveRequest.employee, updatedRequest)
    
    RETURN updatedRequest
END FUNCTION
```

### 3. Leave Balance Management

```pseudocode
FUNCTION deductLeaveBalance(employee, leaveType, days):
    SWITCH leaveType:
        CASE "ANNUAL":
            newBalance = employee.annualLeaveBalance - days
            IF newBalance < 0:
                THROW "Insufficient annual leave balance"
            employee.annualLeaveBalance = newBalance
            
        CASE "SICK":
            newBalance = employee.sickLeaveBalance - days
            IF newBalance < 0:
                THROW "Insufficient sick leave balance"
            employee.sickLeaveBalance = newBalance
            
        CASE "PERSONAL":
            newBalance = employee.personalLeaveBalance - days
            IF newBalance < 0:
                THROW "Insufficient personal leave balance"
            employee.personalLeaveBalance = newBalance
            
        DEFAULT:
            // For special leaves like maternity/paternity, no balance check
            RETURN
    
    saveEmployee(employee)
END FUNCTION

FUNCTION restoreLeaveBalance(employee, leaveType, days):
    SWITCH leaveType:
        CASE "ANNUAL":
            employee.annualLeaveBalance = employee.annualLeaveBalance + days
        CASE "SICK":
            employee.sickLeaveBalance = employee.sickLeaveBalance + days
        CASE "PERSONAL":
            employee.personalLeaveBalance = employee.personalLeaveBalance + days
    
    saveEmployee(employee)
END FUNCTION
```

### 4. Approver Determination Logic

```pseudocode
FUNCTION determineApprover(employee):
    // Primary rule: Direct manager
    IF employee.manager != null AND employee.manager.active == true:
        RETURN employee.manager
    
    // Fallback 1: Department head
    departmentManagers = getManagersByDepartment(employee.department.id)
    IF departmentManagers.size > 0:
        RETURN departmentManagers[0]
    
    // Fallback 2: HR Admin
    hrAdmins = getEmployeesByRole("HR_ADMIN")
    IF hrAdmins.size > 0:
        RETURN hrAdmins[0]
    
    // Fallback 3: Any manager in the system
    systemManagers = getEmployeesByRole("MANAGER")
    IF systemManagers.size > 0:
        RETURN systemManagers[0]
    
    THROW "No approver available in the system"
END FUNCTION
```

### 5. Authority Validation

```pseudocode
FUNCTION checkApprovalAuthority(leaveRequest, approver):
    // HR Admins can approve any leave
    IF approver.role == "HR_ADMIN":
        RETURN true
    
    // Direct manager can approve subordinate's leave
    IF leaveRequest.employee.manager != null AND 
       leaveRequest.employee.manager.id == approver.id:
        RETURN true
    
    // Department managers can approve leaves in their department
    IF approver.role == "MANAGER" AND 
       leaveRequest.employee.department.id == approver.department.id:
        RETURN true
    
    RETURN false
END FUNCTION
```

### 6. Overlap Detection

```pseudocode
FUNCTION findOverlappingLeaves(employeeId, startDate, endDate):
    query = "SELECT * FROM leave_requests WHERE 
             employee_id = ? AND 
             status IN ('PENDING', 'APPROVED') AND 
             (start_date <= ? AND end_date >= ?)"
    
    overlappingRequests = executeQuery(query, [employeeId, endDate, startDate])
    
    RETURN overlappingRequests
END FUNCTION
```

## Key Business Rules

### 1. Leave Application Rules
- **Past Date Prevention**: Cannot apply for leave starting in the past
- **Notice Period**: Minimum advance notice based on leave type
- **Balance Validation**: Sufficient leave balance must be available
- **Overlap Prevention**: No overlapping leave requests allowed
- **Active Employee Only**: Only active employees can apply for leave

### 2. Approval Rules
- **Self-Approval Prohibition**: Managers cannot approve their own leave
- **Authority Validation**: Only authorized personnel can approve leaves
- **Status Progression**: Leaves can only be approved/rejected from PENDING status
- **Balance Deduction**: Approved leaves automatically deduct from balance

### 3. Cancellation Rules
- **Owner Permission**: Employees can cancel their own requests
- **Manager Permission**: Managers can cancel subordinates' requests
- **HR Permission**: HR admins can cancel any request
- **Balance Restoration**: Cancelled approved leaves restore balance

### 4. Notification Rules
- **Application Notification**: Notify assigned approver when leave is applied
- **Decision Notification**: Notify employee when leave is approved/rejected
- **Cancellation Notification**: Notify relevant parties when leave is cancelled

### 5. Audit Trail Rules
- **Complete Logging**: All leave operations must be logged
- **Immutable Records**: Transaction records cannot be modified
- **User Attribution**: All actions must be attributed to a user
- **Timestamp Accuracy**: All events must have accurate timestamps

## Error Handling Strategy

### 1. Validation Errors
- Return HTTP 400 with detailed field-level error messages
- Provide actionable error messages for users
- Include error codes for programmatic handling

### 2. Business Rule Violations
- Return HTTP 409 with business rule explanation
- Suggest alternative actions when possible
- Log rule violations for analysis

### 3. Authorization Errors
- Return HTTP 403 with clear permission messages
- Don't expose sensitive information in error messages
- Log authorization attempts for security monitoring

### 4. System Errors
- Return HTTP 500 with generic error message
- Log detailed error information for debugging
- Implement retry mechanisms for transient failures

## Performance Considerations

### 1. Database Optimization
- Index frequently queried fields (employee_id, status, dates)
- Use pagination for large result sets
- Implement database connection pooling

### 2. Caching Strategy
- Cache employee leave balances for quick access
- Cache department and manager hierarchies
- Implement cache invalidation on updates

### 3. Batch Operations
- Process multiple approvals in batches
- Bulk balance updates for year-end processing
- Batch notification sending

### 4. Asynchronous Processing
- Send notifications asynchronously
- Generate reports in background
- Process large data imports asynchronously