package com.lms.dto;

import com.lms.model.LeaveRequest.LeaveType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for Leave Request creation
 */
public class LeaveRequestCreateDTO {

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 500, message = "Reason must be between 5 and 500 characters")
    private String reason;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // Constructors
    public LeaveRequestCreateDTO() {}

    public LeaveRequestCreateDTO(LocalDate startDate, LocalDate endDate, LeaveType leaveType, String reason, Long employeeId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.employeeId = employeeId;
    }

    // Getters and Setters
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

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "LeaveRequestCreateDTO{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", leaveType=" + leaveType +
                ", reason='" + reason + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }
}