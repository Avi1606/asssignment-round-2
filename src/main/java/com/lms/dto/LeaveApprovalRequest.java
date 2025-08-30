package com.lms.dto;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for Leave Approval/Rejection
 */
public class LeaveApprovalRequest {
    
    private Long approverId;
    
    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;
    
    // Constructors
    public LeaveApprovalRequest() {}
    
    public LeaveApprovalRequest(Long approverId, String comments) {
        this.approverId = approverId;
        this.comments = comments;
    }
    
    // Getters and Setters
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
}