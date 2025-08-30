package com.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Leave Request entity representing a leave application
 */
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;
    
    @Column(nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @Column(nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;
    
    @Column(length = 1000)
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Employee approver;
    
    @Column(name = "approval_comments", length = 1000)
    @Size(max = 1000, message = "Approval comments cannot exceed 1000 characters")
    private String approvalComments;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "days_requested", nullable = false)
    private Integer daysRequested;
    
    @Column(name = "is_emergency", nullable = false)
    private Boolean isEmergency = false;
    
    @OneToMany(mappedBy = "leaveRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LeaveTransaction> transactions;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateDaysRequested();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private void calculateDaysRequested() {
        if (startDate != null && endDate != null) {
            // Simple calculation - in real implementation, would exclude weekends/holidays
            daysRequested = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        }
    }
    
    // Enums
    public enum LeaveType {
        ANNUAL("Annual Leave"),
        SICK("Sick Leave"),
        PERSONAL("Personal Leave"),
        EMERGENCY("Emergency Leave"),
        MATERNITY("Maternity Leave"),
        PATERNITY("Paternity Leave");
        
        private final String displayName;
        
        LeaveType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum LeaveStatus {
        PENDING("Pending"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        LeaveStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public LeaveRequest() {}
    
    public LeaveRequest(Employee employee, LocalDate startDate, LocalDate endDate, 
                      LeaveType leaveType, String reason) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateDaysRequested();
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        calculateDaysRequested();
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
    
    public LeaveStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
    
    public Employee getApprover() {
        return approver;
    }
    
    public void setApprover(Employee approver) {
        this.approver = approver;
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
    
    public List<LeaveTransaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<LeaveTransaction> transactions) {
        this.transactions = transactions;
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