package com.lms.service;

import com.lms.dto.LeaveRequestCreateDTO;
import com.lms.dto.LeaveRequestResponseDTO;
import com.lms.exception.BusinessException;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Leave Request business logic operations
 * 
 * Implements comprehensive leave management including application,
 * approval workflow, balance validation, and business rules.
 */
@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTransactionRepository leaveTransactionRepository;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Apply for leave with comprehensive validation
     */
    public LeaveRequestResponseDTO applyForLeave(LeaveRequestCreateDTO createDTO) {
        // Validate employee exists and is active
        Employee employee = employeeRepository.findById(createDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + createDTO.getEmployeeId()));

        if (!employee.getIsActive()) {
            throw new BusinessException("Cannot apply leave for inactive employee");
        }

        // Validate dates
        validateLeaveDates(createDTO.getStartDate(), createDTO.getEndDate());

        // Check for overlapping leave requests
        checkForOverlappingLeaves(createDTO.getEmployeeId(), createDTO.getStartDate(), createDTO.getEndDate());

        // Validate leave balance
        validateLeaveBalance(employee, createDTO.getLeaveType(), createDTO.getStartDate(), createDTO.getEndDate());

        // Validate business rules (advance notice, etc.)
        validateBusinessRules(createDTO);

        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest(
                createDTO.getStartDate(),
                createDTO.getEndDate(),
                createDTO.getLeaveType(),
                createDTO.getReason(),
                employee
        );

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        // Create transaction log
        createTransaction(LeaveTransaction.TransactionType.LEAVE_APPLIED,
                "Leave application submitted for " + leaveRequest.getDaysRequested() + " days",
                savedRequest, employee);

        return mapToResponseDTO(savedRequest);
    }

    /**
     * Approve leave request
     */
    public LeaveRequestResponseDTO approveLeave(Long leaveRequestId, Long approverId, String comments) {
        // Validate leave request exists and is pending
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveRequestId));

        if (!leaveRequest.isPending()) {
            throw new BusinessException("Leave request is not in pending status");
        }

        // Validate approver
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with ID: " + approverId));

        if (!approver.getIsActive()) {
            throw new BusinessException("Approver is not active");
        }

        // Business rule: Employee cannot approve their own leave
        if (leaveRequest.getEmployee().getId().equals(approverId)) {
            throw new BusinessException("Employee cannot approve their own leave request");
        }

        // Business rule: Only manager can approve subordinate's leave
        if (leaveRequest.getEmployee().getManager() == null || 
            !leaveRequest.getEmployee().getManager().getId().equals(approverId)) {
            throw new BusinessException("Only the employee's direct manager can approve this leave request");
        }

        // Final balance check (in case balance changed after application)
        validateLeaveBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType(), 
                           leaveRequest.getStartDate(), leaveRequest.getEndDate());

        // Approve the leave
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leaveRequest.setApprovedBy(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());
        leaveRequest.setApproverComments(comments);

        LeaveRequest approvedRequest = leaveRequestRepository.save(leaveRequest);

        // Deduct leave balance
        deductLeaveBalance(approvedRequest);

        // Create transaction logs
        createTransaction(LeaveTransaction.TransactionType.LEAVE_APPROVED,
                "Leave request approved by " + approver.getName() + ": " + comments,
                approvedRequest, approver);

        return mapToResponseDTO(approvedRequest);
    }

    /**
     * Reject leave request
     */
    public LeaveRequestResponseDTO rejectLeave(Long leaveRequestId, Long approverId, String comments) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveRequestId));

        if (!leaveRequest.isPending()) {
            throw new BusinessException("Leave request is not in pending status");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with ID: " + approverId));

        // Same validation as approve
        if (leaveRequest.getEmployee().getId().equals(approverId)) {
            throw new BusinessException("Employee cannot reject their own leave request");
        }

        if (leaveRequest.getEmployee().getManager() == null || 
            !leaveRequest.getEmployee().getManager().getId().equals(approverId)) {
            throw new BusinessException("Only the employee's direct manager can reject this leave request");
        }

        // Reject the leave
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leaveRequest.setApprovedBy(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());
        leaveRequest.setApproverComments(comments);

        LeaveRequest rejectedRequest = leaveRequestRepository.save(leaveRequest);

        // Create transaction log
        createTransaction(LeaveTransaction.TransactionType.LEAVE_REJECTED,
                "Leave request rejected by " + approver.getName() + ": " + comments,
                rejectedRequest, approver);

        return mapToResponseDTO(rejectedRequest);
    }

    /**
     * Get all leave requests
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDTO> getAllLeaveRequests() {
        return leaveRequestRepository.findAllWithDetails()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get leave request by ID
     */
    @Transactional(readOnly = true)
    public LeaveRequestResponseDTO getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + id));
        return mapToResponseDTO(leaveRequest);
    }

    /**
     * Get leave requests by employee
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDTO> getLeaveRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending leave requests for a manager
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDTO> getPendingLeavesByManager(Long managerId) {
        return leaveRequestRepository.findPendingLeavesByManager(managerId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private void validateLeaveDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (startDate.isBefore(today)) {
            throw new BusinessException("Leave start date cannot be in the past");
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException("Leave end date cannot be before start date");
        }

        if (ChronoUnit.DAYS.between(startDate, endDate) > 30) {
            throw new BusinessException("Leave duration cannot exceed 30 days");
        }
    }

    private void checkForOverlappingLeaves(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> overlappingLeaves = leaveRequestRepository.findOverlappingLeaves(
                employeeId, startDate, endDate);

        if (!overlappingLeaves.isEmpty()) {
            throw new BusinessException("Leave request overlaps with existing leave(s)");
        }
    }

    private void validateLeaveBalance(Employee employee, LeaveRequest.LeaveType leaveType, 
                                    LocalDate startDate, LocalDate endDate) {
        int daysRequested = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int availableBalance = getAvailableBalance(employee, leaveType);

        if (daysRequested > availableBalance) {
            throw new BusinessException("Insufficient " + leaveType.getDisplayName() + 
                                      " balance. Required: " + daysRequested + 
                                      ", Available: " + availableBalance);
        }
    }

    private int getAvailableBalance(Employee employee, LeaveRequest.LeaveType leaveType) {
        switch (leaveType) {
            case ANNUAL:
                return employee.getAnnualLeaveBalance();
            case SICK:
                return employee.getSickLeaveBalance();
            case CASUAL:
                return employee.getCasualLeaveBalance();
            default:
                return 0; // For special leaves like maternity, paternity
        }
    }

    private void validateBusinessRules(LeaveRequestCreateDTO createDTO) {
        LocalDate today = LocalDate.now();
        long daysUntilStart = ChronoUnit.DAYS.between(today, createDTO.getStartDate());

        // Business rule: Minimum advance notice
        switch (createDTO.getLeaveType()) {
            case SICK:
                // Can be applied on the same day for emergencies, but check if it's not too far in future
                if (daysUntilStart > 5) {
                    throw new BusinessException("Sick leave cannot be applied more than 5 days in advance");
                }
                break;
            case ANNUAL:
                if (daysUntilStart < 7) {
                    throw new BusinessException("Annual leave requires at least 7 days advance notice");
                }
                break;
            case CASUAL:
                if (daysUntilStart < 1) {
                    throw new BusinessException("Casual leave requires at least 1 day advance notice");
                }
                break;
        }
    }

    private void deductLeaveBalance(LeaveRequest leaveRequest) {
        String leaveType = leaveRequest.getLeaveType().name();
        int daysToDeduct = -leaveRequest.getDaysRequested(); // Negative to deduct

        employeeService.updateLeaveBalance(
                leaveRequest.getEmployee().getId(), 
                leaveType, 
                daysToDeduct);

        // Create balance transaction
        createBalanceTransaction(leaveRequest);
    }

    private void createBalanceTransaction(LeaveRequest leaveRequest) {
        Employee employee = leaveRequest.getEmployee();
        int balanceBefore = getAvailableBalance(employee, leaveRequest.getLeaveType()) + leaveRequest.getDaysRequested();
        int balanceAfter = getAvailableBalance(employee, leaveRequest.getLeaveType());

        LeaveTransaction transaction = new LeaveTransaction(
                LeaveTransaction.TransactionType.BALANCE_DEDUCTED,
                "Leave balance deducted for approved " + leaveRequest.getLeaveType().getDisplayName(),
                balanceBefore,
                balanceAfter,
                leaveRequest.getDaysRequested(),
                leaveRequest,
                leaveRequest.getApprovedBy()
        );

        leaveTransactionRepository.save(transaction);
    }

    private void createTransaction(LeaveTransaction.TransactionType type, String description,
                                 LeaveRequest leaveRequest, Employee performedBy) {
        LeaveTransaction transaction = new LeaveTransaction(type, description, leaveRequest, performedBy);
        leaveTransactionRepository.save(transaction);
    }

    private LeaveRequestResponseDTO mapToResponseDTO(LeaveRequest leaveRequest) {
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();
        dto.setId(leaveRequest.getId());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setDaysRequested(leaveRequest.getDaysRequested());
        dto.setApproverComments(leaveRequest.getApproverComments());
        dto.setApprovedAt(leaveRequest.getApprovedAt());
        dto.setCreatedAt(leaveRequest.getCreatedAt());
        dto.setUpdatedAt(leaveRequest.getUpdatedAt());

        // Set employee info
        if (leaveRequest.getEmployee() != null) {
            LeaveRequestResponseDTO.EmployeeBasicDTO employeeDTO = new LeaveRequestResponseDTO.EmployeeBasicDTO(
                    leaveRequest.getEmployee().getId(),
                    leaveRequest.getEmployee().getName(),
                    leaveRequest.getEmployee().getEmail()
            );
            dto.setEmployee(employeeDTO);
        }

        // Set approver info
        if (leaveRequest.getApprovedBy() != null) {
            LeaveRequestResponseDTO.EmployeeBasicDTO approverDTO = new LeaveRequestResponseDTO.EmployeeBasicDTO(
                    leaveRequest.getApprovedBy().getId(),
                    leaveRequest.getApprovedBy().getName(),
                    leaveRequest.getApprovedBy().getEmail()
            );
            dto.setApprovedBy(approverDTO);
        }

        return dto;
    }
}