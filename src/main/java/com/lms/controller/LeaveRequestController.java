package com.lms.controller;

import com.lms.dto.LeaveApprovalDto;
import com.lms.dto.LeaveRequestDto;
import com.lms.model.LeaveStatus;
import com.lms.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Leave Request operations
 * 
 * @author Avi Patel
 */
@RestController
@RequestMapping("/leave-requests")
@Tag(name = "Leave Request Management", description = "APIs for managing employee leave requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @Autowired
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @Operation(summary = "Apply for leave", 
               description = "Submit a new leave request with validation for business rules")
    @PostMapping
    public ResponseEntity<LeaveRequestDto> applyForLeave(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveRequestDto created = leaveRequestService.applyForLeave(leaveRequestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get leave request by ID")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> getLeaveRequestById(
            @Parameter(description = "Leave request ID") @PathVariable Long id) {
        LeaveRequestDto leaveRequest = leaveRequestService.getLeaveRequestById(id);
        return ResponseEntity.ok(leaveRequest);
    }

    @Operation(summary = "Get leave requests by employee")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveRequestsByEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long employeeId) {
        List<LeaveRequestDto> requests = leaveRequestService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Get pending leave requests for manager")
    @GetMapping("/manager/{managerId}/pending")
    public ResponseEntity<List<LeaveRequestDto>> getPendingLeaveRequestsForManager(
            @Parameter(description = "Manager ID") @PathVariable Long managerId) {
        List<LeaveRequestDto> requests = leaveRequestService.getPendingLeaveRequestsForManager(managerId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Process leave approval/rejection",
               description = "Approve or reject a leave request with proper validation")
    @PatchMapping("/approval")
    public ResponseEntity<LeaveRequestDto> processLeaveApproval(@Valid @RequestBody LeaveApprovalDto approvalDto) {
        LeaveRequestDto processed = leaveRequestService.processLeaveApproval(approvalDto);
        return ResponseEntity.ok(processed);
    }

    @Operation(summary = "Cancel leave request")
    @PatchMapping("/{leaveRequestId}/cancel")
    public ResponseEntity<LeaveRequestDto> cancelLeaveRequest(
            @Parameter(description = "Leave request ID") @PathVariable Long leaveRequestId,
            @Parameter(description = "Employee ID requesting cancellation") @RequestParam Long employeeId,
            @Parameter(description = "Reason for cancellation") @RequestParam(required = false) String reason) {
        
        LeaveRequestDto cancelled = leaveRequestService.cancelLeaveRequest(leaveRequestId, employeeId, reason);
        return ResponseEntity.ok(cancelled);
    }

    @Operation(summary = "Get leave requests by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveRequestsByStatus(
            @Parameter(description = "Leave status") @PathVariable LeaveStatus status) {
        List<LeaveRequestDto> requests = leaveRequestService.getLeaveRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Get approved leaves between dates")
    @GetMapping("/approved")
    public ResponseEntity<List<LeaveRequestDto>> getApprovedLeavesBetweenDates(
            @Parameter(description = "Start date (YYYY-MM-DD)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<LeaveRequestDto> requests = leaveRequestService.getApprovedLeavesBetweenDates(startDate, endDate);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Get all pending leave requests")
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDto>> getAllPendingLeaveRequests() {
        List<LeaveRequestDto> requests = leaveRequestService.getLeaveRequestsByStatus(LeaveStatus.PENDING);
        return ResponseEntity.ok(requests);
    }
}