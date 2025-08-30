package com.lms.service;

import com.lms.dto.LeaveApprovalRequest;
import com.lms.dto.LeaveRequestRequest;
import com.lms.dto.LeaveRequestResponse;
import com.lms.exception.BusinessRuleException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.Employee;
import com.lms.model.LeaveRequest;
import com.lms.model.LeaveTransaction;
import com.lms.repository.EmployeeRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.LeaveTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Leave Request management operations
 */
@Service
@Transactional
public class LeaveRequestService {
    
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTransactionRepository leaveTransactionRepository;
    
    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                              EmployeeRepository employeeRepository,
                              LeaveTransactionRepository leaveTransactionRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTransactionRepository = leaveTransactionRepository;
    }
    
    /**
     * Apply for leave
     * @param request the leave request
     * @return the created leave request response
     */
    public LeaveRequestResponse applyForLeave(LeaveRequestRequest request) {
        // Find employee
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        // Validate business rules
        validateLeaveRequest(request, employee);
        
        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest(
                employee,
                request.getStartDate(),
                request.getEndDate(),
                request.getLeaveType(),
                request.getReason()
        );
        
        leaveRequest.setIsEmergency(request.getIsEmergency());
        
        // Auto-assign approver (manager)
        if (employee.getManager() != null) {
            leaveRequest.setApprover(employee.getManager());
        }
        
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        
        // Create audit transaction
        createLeaveTransaction(savedRequest, employee, employee, 
                LeaveTransaction.TransactionType.CREATED,
                "Leave request created");
        
        return new LeaveRequestResponse(savedRequest);
    }
    
    /**
     * Get all leave requests
     * @return list of leave request responses
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getAllLeaveRequests() {
        return leaveRequestRepository.findAll()
                .stream()
                .map(LeaveRequestResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get leave request by ID
     * @param id the leave request ID
     * @return the leave request response
     */
    @Transactional(readOnly = true)
    public LeaveRequestResponse getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", "id", id));
        return new LeaveRequestResponse(leaveRequest);
    }
    
    /**
     * Get leave requests by employee
     * @param employeeId the employee ID
     * @return list of leave request responses
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getLeaveRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId)
                .stream()
                .map(LeaveRequestResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get pending leave requests for manager
     * @param managerId the manager ID
     * @return list of pending leave request responses
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getPendingLeaveRequestsForManager(Long managerId) {
        return leaveRequestRepository.findPendingLeaveRequestsForManager(managerId)
                .stream()
                .map(LeaveRequestResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Approve leave request
     * @param id the leave request ID
     * @param approvalRequest the approval request details
     * @return the updated leave request response
     */
    public LeaveRequestResponse approveLeaveRequest(Long id, LeaveApprovalRequest approvalRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", "id", id));
        
        // Validate approval
        validateLeaveApproval(leaveRequest, approvalRequest);
        
        // Find approver
        Employee approver = null;
        if (approvalRequest.getApproverId() != null) {
            approver = employeeRepository.findById(approvalRequest.getApproverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver", "id", approvalRequest.getApproverId()));
        }
        
        // Update leave request
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leaveRequest.setApprover(approver);
        leaveRequest.setApprovalComments(approvalRequest.getComments());
        leaveRequest.setApprovedAt(LocalDateTime.now());
        
        // Update employee leave balance
        updateEmployeeLeaveBalance(leaveRequest, false);
        
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        
        // Create audit transaction
        createLeaveTransaction(updatedRequest, leaveRequest.getEmployee(), approver,
                LeaveTransaction.TransactionType.APPROVED,
                "Leave request approved: " + approvalRequest.getComments());
        
        return new LeaveRequestResponse(updatedRequest);
    }
    
    /**
     * Reject leave request
     * @param id the leave request ID
     * @param approvalRequest the rejection request details
     * @return the updated leave request response
     */
    public LeaveRequestResponse rejectLeaveRequest(Long id, LeaveApprovalRequest approvalRequest) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", "id", id));
        
        // Validate rejection
        if (leaveRequest.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessRuleException("Only pending leave requests can be rejected");
        }
        
        // Find approver
        Employee approver = null;
        if (approvalRequest.getApproverId() != null) {
            approver = employeeRepository.findById(approvalRequest.getApproverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver", "id", approvalRequest.getApproverId()));
        }
        
        // Update leave request
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leaveRequest.setApprover(approver);
        leaveRequest.setApprovalComments(approvalRequest.getComments());
        leaveRequest.setApprovedAt(LocalDateTime.now());
        
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        
        // Create audit transaction
        createLeaveTransaction(updatedRequest, leaveRequest.getEmployee(), approver,
                LeaveTransaction.TransactionType.REJECTED,
                "Leave request rejected: " + approvalRequest.getComments());
        
        return new LeaveRequestResponse(updatedRequest);
    }
    
    /**
     * Cancel leave request
     * @param id the leave request ID
     * @param employeeId the employee ID (for authorization)
     * @return the updated leave request response
     */
    public LeaveRequestResponse cancelLeaveRequest(Long id, Long employeeId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", "id", id));
        
        // Validate cancellation
        if (!leaveRequest.getEmployee().getId().equals(employeeId)) {
            throw new BusinessRuleException("Only the employee who created the request can cancel it");
        }
        
        if (leaveRequest.getStatus() == LeaveRequest.LeaveStatus.CANCELLED) {
            throw new BusinessRuleException("Leave request is already cancelled");
        }
        
        // If request was approved, restore leave balance
        if (leaveRequest.getStatus() == LeaveRequest.LeaveStatus.APPROVED) {
            updateEmployeeLeaveBalance(leaveRequest, true);
        }
        
        // Update leave request
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        
        // Create audit transaction
        createLeaveTransaction(updatedRequest, leaveRequest.getEmployee(), leaveRequest.getEmployee(),
                LeaveTransaction.TransactionType.CANCELLED,
                "Leave request cancelled by employee");
        
        return new LeaveRequestResponse(updatedRequest);
    }
    
    // Private helper methods
    
    private void validateLeaveRequest(LeaveRequestRequest request, Employee employee) {
        // Validate date range
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessRuleException("Start date cannot be after end date");
        }
        
        if (request.getStartDate().isBefore(LocalDate.now()) && !request.getIsEmergency()) {
            throw new BusinessRuleException("Cannot apply for past dates unless it's an emergency");
        }
        
        // Check for overlapping leave requests
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequests(
                employee.getId(), request.getStartDate(), request.getEndDate());
        
        if (!overlappingRequests.isEmpty()) {
            throw new BusinessRuleException("You have overlapping leave requests for the selected dates");
        }
        
        // Calculate days and validate balance
        int daysRequested = (int) (request.getEndDate().toEpochDay() - request.getStartDate().toEpochDay() + 1);
        
        switch (request.getLeaveType()) {
            case ANNUAL:
                if (employee.getAvailableAnnualLeave() < daysRequested) {
                    throw new BusinessRuleException("Insufficient annual leave balance. Available: " 
                            + employee.getAvailableAnnualLeave() + " days, Requested: " + daysRequested + " days");
                }
                break;
            case SICK:
                if (employee.getAvailableSickLeave() < daysRequested) {
                    throw new BusinessRuleException("Insufficient sick leave balance. Available: " 
                            + employee.getAvailableSickLeave() + " days, Requested: " + daysRequested + " days");
                }
                break;
            // Other leave types might have different validation rules
        }
        
        // Validate advance notice requirements (skip for emergency)
        if (!request.getIsEmergency()) {
            long daysInAdvance = LocalDate.now().until(request.getStartDate()).getDays();
            
            switch (request.getLeaveType()) {
                case SICK:
                    // Sick leave typically requires minimal advance notice
                    break;
                case ANNUAL:
                    if (daysInAdvance < 7) {
                        throw new BusinessRuleException("Annual leave requires at least 7 days advance notice");
                    }
                    break;
            }
        }
    }
    
    private void validateLeaveApproval(LeaveRequest leaveRequest, LeaveApprovalRequest approvalRequest) {
        if (leaveRequest.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessRuleException("Only pending leave requests can be approved");
        }
        
        // Validate approver authority (if specified)
        if (approvalRequest.getApproverId() != null) {
            Employee approver = employeeRepository.findById(approvalRequest.getApproverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver", "id", approvalRequest.getApproverId()));
            
            // Check if approver is the employee's manager or has authority
            if (leaveRequest.getEmployee().getManager() != null &&
                !leaveRequest.getEmployee().getManager().getId().equals(approver.getId())) {
                // For now, allow any employee to approve, but in production this would be stricter
            }
            
            // Employee cannot approve their own leave
            if (leaveRequest.getEmployee().getId().equals(approver.getId())) {
                throw new BusinessRuleException("Employee cannot approve their own leave request");
            }
        }
    }
    
    private void updateEmployeeLeaveBalance(LeaveRequest leaveRequest, boolean restore) {
        Employee employee = leaveRequest.getEmployee();
        int days = leaveRequest.getDaysRequested();
        
        if (restore) {
            days = -days; // Restore means subtract the negative (add back)
        }
        
        switch (leaveRequest.getLeaveType()) {
            case ANNUAL:
                employee.setUsedAnnualLeave(employee.getUsedAnnualLeave() + days);
                break;
            case SICK:
                employee.setUsedSickLeave(employee.getUsedSickLeave() + days);
                break;
            // Handle other leave types as needed
        }
        
        employeeRepository.save(employee);
    }
    
    private void createLeaveTransaction(LeaveRequest leaveRequest, Employee employee, Employee performedBy,
                                      LeaveTransaction.TransactionType transactionType, String comments) {
        LeaveTransaction transaction = new LeaveTransaction(leaveRequest, employee, performedBy, transactionType, comments);
        transaction.setLeaveDaysAffected(leaveRequest.getDaysRequested());
        leaveTransactionRepository.save(transaction);
    }
}