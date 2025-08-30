package com.lms.dto;

import com.lms.model.LeaveRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Leave Request responses
 */
public class LeaveRequestResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeId_string;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveRequest.LeaveType leaveType;
    private String reason;
    private LeaveRequest.LeaveStatus status;
    private Long approverId;
    private String approverName;
    private String approvalComments;
    private LocalDateTime approvedAt;
    private Integer daysRequested;
    private Boolean isEmergency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public LeaveRequestResponse() {}
    
    public LeaveRequestResponse(LeaveRequest leaveRequest) {
        this.id = leaveRequest.getId();
        this.employeeId = leaveRequest.getEmployee().getId();
        this.employeeName = leaveRequest.getEmployee().getFullName();
        this.employeeId_string = leaveRequest.getEmployee().getEmployeeId();
        this.startDate = leaveRequest.getStartDate();
        this.endDate = leaveRequest.getEndDate();
        this.leaveType = leaveRequest.getLeaveType();
        this.reason = leaveRequest.getReason();
        this.status = leaveRequest.getStatus();
        this.approverId = leaveRequest.getApprover() != null ? leaveRequest.getApprover().getId() : null;
        this.approverName = leaveRequest.getApprover() != null ? leaveRequest.getApprover().getFullName() : null;
        this.approvalComments = leaveRequest.getApprovalComments();
        this.approvedAt = leaveRequest.getApprovedAt();
        this.daysRequested = leaveRequest.getDaysRequested();
        this.isEmergency = leaveRequest.getIsEmergency();
        this.createdAt = leaveRequest.getCreatedAt();
        this.updatedAt = leaveRequest.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public String getEmployeeId_string() {
        return employeeId_string;
    }
    
    public void setEmployeeId_string(String employeeId_string) {
        this.employeeId_string = employeeId_string;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LeaveRequest.LeaveType getLeaveType() {
        return leaveType;
    }
    
    public void setLeaveType(LeaveRequest.LeaveType leaveType) {
        this.leaveType = leaveType;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LeaveRequest.LeaveStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaveRequest.LeaveStatus status) {
        this.status = status;
    }
    
    public Long getApproverId() {
        return approverId;
    }
    
    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }
    
    public String getApproverName() {
        return approverName;
    }
    
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
    
    public String getApprovalComments() {
        return approvalComments;
    }
    
    public void setApprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public Integer getDaysRequested() {
        return daysRequested;
    }
    
    public void setDaysRequested(Integer daysRequested) {
        this.daysRequested = daysRequested;
    }
    
    public Boolean getIsEmergency() {
        return isEmergency;
    }
    
    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}