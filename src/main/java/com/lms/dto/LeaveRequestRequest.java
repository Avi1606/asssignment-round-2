package com.lms.dto;

import com.lms.model.LeaveRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Data Transfer Object for Leave Request creation and updates
 */
public class LeaveRequestRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @NotNull(message = "Leave type is required")
    private LeaveRequest.LeaveType leaveType;
    
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;
    
    private Boolean isEmergency = false;
    
    // Constructors
    public LeaveRequestRequest() {}
    
    public LeaveRequestRequest(Long employeeId, LocalDate startDate, LocalDate endDate,
                             LeaveRequest.LeaveType leaveType, String reason) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
    
    public Boolean getIsEmergency() {
        return isEmergency;
    }
    
    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }
}