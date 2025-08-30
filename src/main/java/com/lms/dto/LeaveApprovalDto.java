package com.lms.dto;

import com.lms.model.LeaveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for Leave Request approval/rejection
 * 
 * @author Avi Patel
 */
public class LeaveApprovalDto {

    @NotNull(message = "Leave request ID is required")
    private Long leaveRequestId;

    @NotNull(message = "Decision is required")
    private LeaveStatus decision; // APPROVED or REJECTED

    @NotNull(message = "Approver ID is required")  
    private Long approverId;

    @Size(max = 500, message = "Comments cannot exceed 500 characters")
    private String comments;

    // Constructors
    public LeaveApprovalDto() {}

    public LeaveApprovalDto(Long leaveRequestId, LeaveStatus decision, Long approverId, String comments) {
        this.leaveRequestId = leaveRequestId;
        this.decision = decision;
        this.approverId = approverId;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(Long leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public LeaveStatus getDecision() {
        return decision;
    }

    public void setDecision(LeaveStatus decision) {
        this.decision = decision;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "LeaveApprovalDto{" +
                "leaveRequestId=" + leaveRequestId +
                ", decision=" + decision +
                ", approverId=" + approverId +
                ", comments='" + comments + '\'' +
                '}';
    }
}