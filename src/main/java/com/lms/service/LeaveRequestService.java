package com.lms.service;

import com.lms.dto.LeaveApprovalDto;
import com.lms.dto.LeaveRequestDto;
import com.lms.exception.LeaveManagementException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.*;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.LeaveTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Leave Request operations with business logic
 * 
 * @author Avi Patel
 */
@Service
@Transactional
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTransactionRepository leaveTransactionRepository;
    private final EmployeeService employeeService;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                             LeaveTransactionRepository leaveTransactionRepository,
                             EmployeeService employeeService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTransactionRepository = leaveTransactionRepository;
        this.employeeService = employeeService;
    }

    /**
     * Apply for leave with comprehensive validation
     */
    public LeaveRequestDto applyForLeave(LeaveRequestDto leaveRequestDto) {
        Employee employee = employeeService.getEmployeeEntity(leaveRequestDto.getEmployeeId());
        
        // Validate the leave request
        validateLeaveRequest(leaveRequestDto, employee);
        
        // Create leave request entity
        LeaveRequest leaveRequest = new LeaveRequest(
                employee,
                leaveRequestDto.getLeaveType(),
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate(),
                leaveRequestDto.getReason()
        );

        // Save the leave request
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        // Create audit transaction
        LeaveTransaction transaction = new LeaveTransaction(
                savedRequest,
                employee,
                TransactionType.LEAVE_APPLIED,
                "Leave application submitted"
        );
        leaveTransactionRepository.save(transaction);

        return convertToDto(savedRequest);
    }

    /**
     * Get all leave requests for an employee
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getLeaveRequestsByEmployee(Long employeeId) {
        List<LeaveRequest> requests = leaveRequestRepository.findByEmployeeId(employeeId);
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get pending leave requests for a manager
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getPendingLeaveRequestsForManager(Long managerId) {
        List<LeaveRequest> requests = leaveRequestRepository
                .findByManagerIdAndStatus(managerId, LeaveStatus.PENDING);
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get leave request by ID
     */
    @Transactional(readOnly = true)
    public LeaveRequestDto getLeaveRequestById(Long id) {
        LeaveRequest request = leaveRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", id));
        return convertToDto(request);
    }

    /**
     * Approve or reject leave request
     */
    public LeaveRequestDto processLeaveApproval(LeaveApprovalDto approvalDto) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(approvalDto.getLeaveRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", approvalDto.getLeaveRequestId()));

        Employee approver = employeeService.getEmployeeEntity(approvalDto.getApproverId());

        // Validate approval authority
        validateApprovalAuthority(leaveRequest, approver);

        // Validate current status
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveManagementException("Leave request is not in pending status");
        }

        LeaveStatus oldStatus = leaveRequest.getStatus();
        
        // Process approval/rejection
        if (approvalDto.getDecision() == LeaveStatus.APPROVED) {
            approveLeave(leaveRequest, approver, approvalDto.getComments());
        } else if (approvalDto.getDecision() == LeaveStatus.REJECTED) {
            rejectLeave(leaveRequest, approver, approvalDto.getComments());
        } else {
            throw new IllegalArgumentException("Invalid decision. Must be APPROVED or REJECTED");
        }

        // Create audit transaction
        LeaveTransaction transaction = new LeaveTransaction(
                leaveRequest,
                approver,
                approvalDto.getDecision() == LeaveStatus.APPROVED ? 
                    TransactionType.LEAVE_APPROVED : TransactionType.LEAVE_REJECTED,
                approvalDto.getComments()
        );
        transaction.setOldStatus(oldStatus);
        transaction.setNewStatus(leaveRequest.getStatus());
        leaveTransactionRepository.save(transaction);

        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        return convertToDto(updatedRequest);
    }

    /**
     * Cancel leave request
     */
    public LeaveRequestDto cancelLeaveRequest(Long leaveRequestId, Long employeeId, String reason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveRequestId));

        Employee employee = employeeService.getEmployeeEntity(employeeId);

        // Validate cancellation authority
        if (!leaveRequest.getEmployee().getId().equals(employeeId) && 
            !employee.canApproveLeaves()) {
            throw new LeaveManagementException("You don't have permission to cancel this leave request");
        }

        // Validate current status
        if (leaveRequest.getStatus() == LeaveStatus.CANCELLED) {
            throw new LeaveManagementException("Leave request is already cancelled");
        }

        LeaveStatus oldStatus = leaveRequest.getStatus();

        // If approved leave is being cancelled, restore leave balance
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED) {
            restoreLeaveBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType(), leaveRequest.getTotalDays());
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setApprovalComments(reason);

        // Create audit transaction
        LeaveTransaction transaction = new LeaveTransaction(
                leaveRequest,
                employee,
                TransactionType.LEAVE_CANCELLED,
                reason
        );
        transaction.setOldStatus(oldStatus);
        transaction.setNewStatus(LeaveStatus.CANCELLED);
        leaveTransactionRepository.save(transaction);

        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        return convertToDto(updatedRequest);
    }

    /**
     * Get leave requests by status
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getLeaveRequestsByStatus(LeaveStatus status) {
        List<LeaveRequest> requests = leaveRequestRepository.findByStatus(status);
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get approved leaves between dates
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getApprovedLeavesBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> requests = leaveRequestRepository.findApprovedLeavesBetweenDates(startDate, endDate);
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private void validateLeaveRequest(LeaveRequestDto leaveRequestDto, Employee employee) {
        LocalDate startDate = leaveRequestDto.getStartDate();
        LocalDate endDate = leaveRequestDto.getEndDate();
        LeaveType leaveType = leaveRequestDto.getLeaveType();

        // Validate dates
        if (startDate.isBefore(LocalDate.now())) {
            throw new LeaveManagementException("Start date cannot be in the past");
        }

        if (endDate.isBefore(startDate)) {
            throw new LeaveManagementException("End date must be after start date");
        }

        // Validate notice period
        long daysNotice = ChronoUnit.DAYS.between(LocalDate.now(), startDate);
        if (leaveType == LeaveType.SICK && daysNotice < 1) {
            throw new LeaveManagementException("Minimum 1-day notice required for sick leave");
        }
        if (leaveType == LeaveType.ANNUAL && daysNotice < 7) {
            throw new LeaveManagementException("Minimum 7-day advance notice required for vacation leave");
        }

        // Calculate total days
        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Check leave balance
        validateLeaveBalance(employee, leaveType, totalDays);

        // Check for overlapping requests
        List<LeaveRequest> overlappingRequests = leaveRequestRepository
                .findOverlappingLeaves(employee.getId(), startDate, endDate);
        
        if (!overlappingRequests.isEmpty()) {
            throw new LeaveManagementException("You have overlapping leave requests for the selected dates");
        }
    }

    private void validateLeaveBalance(Employee employee, LeaveType leaveType, int requestedDays) {
        int availableBalance = getAvailableBalance(employee, leaveType);
        
        if (requestedDays > availableBalance) {
            throw new LeaveManagementException(
                String.format("Insufficient leave balance. Available: %d days, Requested: %d days", 
                            availableBalance, requestedDays)
            );
        }
    }

    private int getAvailableBalance(Employee employee, LeaveType leaveType) {
        switch (leaveType) {
            case ANNUAL:
                return employee.getAnnualLeaveBalance();
            case SICK:
                return employee.getSickLeaveBalance();
            case PERSONAL:
                return employee.getPersonalLeaveBalance();
            default:
                return 0; // For other types like maternity, paternity
        }
    }

    private void validateApprovalAuthority(LeaveRequest leaveRequest, Employee approver) {
        // Manager cannot approve own leave
        if (leaveRequest.getEmployee().getId().equals(approver.getId())) {
            throw new LeaveManagementException("You cannot approve your own leave request");
        }

        // Check if approver has authority
        boolean hasAuthority = approver.canApproveLeaves() || 
                             (leaveRequest.getEmployee().getManager() != null && 
                              leaveRequest.getEmployee().getManager().getId().equals(approver.getId()));

        if (!hasAuthority) {
            throw new LeaveManagementException("You don't have permission to approve this leave request");
        }
    }

    private void approveLeave(LeaveRequest leaveRequest, Employee approver, String comments) {
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setApprovedBy(approver);
        leaveRequest.setApprovalDate(LocalDateTime.now());
        leaveRequest.setApprovalComments(comments);

        // Deduct leave balance
        deductLeaveBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType(), leaveRequest.getTotalDays());
    }

    private void rejectLeave(LeaveRequest leaveRequest, Employee approver, String comments) {
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setApprovedBy(approver);
        leaveRequest.setApprovalDate(LocalDateTime.now());
        leaveRequest.setApprovalComments(comments);
    }

    private void deductLeaveBalance(Employee employee, LeaveType leaveType, int days) {
        switch (leaveType) {
            case ANNUAL:
                employee.setAnnualLeaveBalance(employee.getAnnualLeaveBalance() - days);
                break;
            case SICK:
                employee.setSickLeaveBalance(employee.getSickLeaveBalance() - days);
                break;
            case PERSONAL:
                employee.setPersonalLeaveBalance(employee.getPersonalLeaveBalance() - days);
                break;
        }
        employeeService.updateEmployee(employee.getId(), convertEmployeeToDto(employee));
    }

    private void restoreLeaveBalance(Employee employee, LeaveType leaveType, int days) {
        switch (leaveType) {
            case ANNUAL:
                employee.setAnnualLeaveBalance(employee.getAnnualLeaveBalance() + days);
                break;
            case SICK:
                employee.setSickLeaveBalance(employee.getSickLeaveBalance() + days);
                break;
            case PERSONAL:
                employee.setPersonalLeaveBalance(employee.getPersonalLeaveBalance() + days);
                break;
        }
        employeeService.updateEmployee(employee.getId(), convertEmployeeToDto(employee));
    }

    private LeaveRequestDto convertToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployee().getId());
        dto.setEmployeeName(leaveRequest.getEmployee().getFullName());
        dto.setDepartmentName(leaveRequest.getEmployee().getDepartment().getName());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setTotalDays(leaveRequest.getTotalDays());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setCreatedAt(leaveRequest.getCreatedAt());
        dto.setUpdatedAt(leaveRequest.getUpdatedAt());

        if (leaveRequest.getApprovedBy() != null) {
            dto.setApprovedById(leaveRequest.getApprovedBy().getId());
            dto.setApprovedByName(leaveRequest.getApprovedBy().getFullName());
        }
        if (leaveRequest.getApprovalDate() != null) {
            dto.setApprovalDate(leaveRequest.getApprovalDate());
        }
        if (leaveRequest.getApprovalComments() != null) {
            dto.setApprovalComments(leaveRequest.getApprovalComments());
        }

        return dto;
    }

    private com.lms.dto.EmployeeDto convertEmployeeToDto(Employee employee) {
        // Simple conversion for internal use
        com.lms.dto.EmployeeDto dto = new com.lms.dto.EmployeeDto();
        dto.setId(employee.getId());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setDepartmentId(employee.getDepartment().getId());
        dto.setRole(employee.getRole());
        dto.setAnnualLeaveBalance(employee.getAnnualLeaveBalance());
        dto.setSickLeaveBalance(employee.getSickLeaveBalance());
        dto.setPersonalLeaveBalance(employee.getPersonalLeaveBalance());
        dto.setActive(employee.getActive());
        if (employee.getManager() != null) {
            dto.setManagerId(employee.getManager().getId());
        }
        return dto;
    }
}