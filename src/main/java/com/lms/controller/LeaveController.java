package com.lms.controller;

import com.lms.dto.LeaveRequestCreateDTO;
import com.lms.dto.LeaveRequestResponseDTO;
import com.lms.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Leave Request management operations
 * 
 * Provides endpoints for leave application, approval workflow,
 * and leave request tracking with comprehensive business logic.
 */
@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leave Management", description = "APIs for managing leave requests and approvals")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Operation(summary = "Apply for leave", description = "Submit a new leave request with validation")
    @PostMapping
    public ResponseEntity<LeaveRequestResponseDTO> applyForLeave(@Valid @RequestBody LeaveRequestCreateDTO createDTO) {
        LeaveRequestResponseDTO leaveRequest = leaveService.applyForLeave(createDTO);
        return new ResponseEntity<>(leaveRequest, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all leave requests", description = "Retrieves all leave requests in the system")
    @GetMapping
    public ResponseEntity<List<LeaveRequestResponseDTO>> getAllLeaveRequests() {
        List<LeaveRequestResponseDTO> leaveRequests = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(leaveRequests);
    }

    @Operation(summary = "Get leave request by ID", description = "Retrieves a specific leave request by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestResponseDTO> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequestResponseDTO leaveRequest = leaveService.getLeaveRequestById(id);
        return ResponseEntity.ok(leaveRequest);
    }

    @Operation(summary = "Approve leave request", description = "Approve a pending leave request")
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestResponseDTO> approveLeave(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {
        LeaveRequestResponseDTO approvedLeave = leaveService.approveLeave(id, approverId, comments);
        return ResponseEntity.ok(approvedLeave);
    }

    @Operation(summary = "Reject leave request", description = "Reject a pending leave request")
    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestResponseDTO> rejectLeave(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {
        LeaveRequestResponseDTO rejectedLeave = leaveService.rejectLeave(id, approverId, comments);
        return ResponseEntity.ok(rejectedLeave);
    }

    @Operation(summary = "Get employee leave requests", description = "Retrieves all leave requests for a specific employee")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        List<LeaveRequestResponseDTO> leaveRequests = leaveService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(leaveRequests);
    }

    @Operation(summary = "Get pending leaves by manager", description = "Retrieves pending leave requests for a manager's approval")
    @GetMapping("/pending/manager/{managerId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getPendingLeavesByManager(@PathVariable Long managerId) {
        List<LeaveRequestResponseDTO> pendingLeaves = leaveService.getPendingLeavesByManager(managerId);
        return ResponseEntity.ok(pendingLeaves);
    }
}