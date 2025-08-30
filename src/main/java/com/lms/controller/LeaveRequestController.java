package com.lms.controller;

import com.lms.dto.LeaveApprovalRequest;
import com.lms.dto.LeaveRequestRequest;
import com.lms.dto.LeaveRequestResponse;
import com.lms.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Leave Request management operations
 */
@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leave Management", description = "APIs for managing leave requests")
public class LeaveRequestController {
    
    private final LeaveRequestService leaveRequestService;
    
    @Autowired
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }
    
    /**
     * Apply for leave
     */
    @PostMapping
    @Operation(summary = "Apply for Leave", description = "Submit a new leave request")
    public ResponseEntity<LeaveRequestResponse> applyForLeave(@Valid @RequestBody LeaveRequestRequest request) {
        LeaveRequestResponse response = leaveRequestService.applyForLeave(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get all leave requests
     */
    @GetMapping
    @Operation(summary = "Get All Leave Requests", description = "Retrieve all leave requests in the system")
    public ResponseEntity<List<LeaveRequestResponse>> getAllLeaveRequests() {
        List<LeaveRequestResponse> leaveRequests = leaveRequestService.getAllLeaveRequests();
        return ResponseEntity.ok(leaveRequests);
    }
    
    /**
     * Get leave request by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Leave Request by ID", description = "Retrieve a specific leave request by its ID")
    public ResponseEntity<LeaveRequestResponse> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequestResponse leaveRequest = leaveRequestService.getLeaveRequestById(id);
        return ResponseEntity.ok(leaveRequest);
    }
    
    /**
     * Get leave requests by employee
     */
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get Leave Requests by Employee", description = "Retrieve all leave requests for a specific employee")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        List<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(leaveRequests);
    }
    
    /**
     * Get pending leave requests for manager
     */
    @GetMapping("/manager/{managerId}/pending")
    @Operation(summary = "Get Pending Leave Requests for Manager", description = "Retrieve pending leave requests for manager approval")
    public ResponseEntity<List<LeaveRequestResponse>> getPendingLeaveRequestsForManager(@PathVariable Long managerId) {
        List<LeaveRequestResponse> pendingRequests = leaveRequestService.getPendingLeaveRequestsForManager(managerId);
        return ResponseEntity.ok(pendingRequests);
    }
    
    /**
     * Approve leave request
     */
    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve Leave Request", description = "Approve a pending leave request")
    public ResponseEntity<LeaveRequestResponse> approveLeaveRequest(@PathVariable Long id, 
                                                                  @Valid @RequestBody LeaveApprovalRequest approvalRequest) {
        LeaveRequestResponse response = leaveRequestService.approveLeaveRequest(id, approvalRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reject leave request
     */
    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject Leave Request", description = "Reject a pending leave request")
    public ResponseEntity<LeaveRequestResponse> rejectLeaveRequest(@PathVariable Long id, 
                                                                 @Valid @RequestBody LeaveApprovalRequest approvalRequest) {
        LeaveRequestResponse response = leaveRequestService.rejectLeaveRequest(id, approvalRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel leave request
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel Leave Request", description = "Cancel a leave request (employee only)")
    public ResponseEntity<LeaveRequestResponse> cancelLeaveRequest(@PathVariable Long id, 
                                                                 @RequestParam Long employeeId) {
        LeaveRequestResponse response = leaveRequestService.cancelLeaveRequest(id, employeeId);
        return ResponseEntity.ok(response);
    }
}