package com.lms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Leave Transaction entity for audit trail of all leave-related changes
 */
@Entity
@Table(name = "leave_transactions")
public class LeaveTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    private LeaveRequest leaveRequest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private Employee performedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;
    
    @Column(name = "old_status")
    private String oldStatus;
    
    @Column(name = "new_status")
    private String newStatus;
    
    @Column(length = 1000)
    private String comments;
    
    @Column(name = "leave_days_affected")
    private Integer leaveDaysAffected;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Enums
    public enum TransactionType {
        CREATED("Leave Request Created"),
        APPROVED("Leave Request Approved"),
        REJECTED("Leave Request Rejected"),
        CANCELLED("Leave Request Cancelled"),
        UPDATED("Leave Request Updated"),
        BALANCE_ADJUSTED("Leave Balance Adjusted");
        
        private final String displayName;
        
        TransactionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public LeaveTransaction() {}
    
    public LeaveTransaction(LeaveRequest leaveRequest, Employee employee, Employee performedBy,
                          TransactionType transactionType, String comments) {
        this.leaveRequest = leaveRequest;
        this.employee = employee;
        this.performedBy = performedBy;
        this.transactionType = transactionType;
        this.comments = comments;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LeaveRequest getLeaveRequest() {
        return leaveRequest;
    }
    
    public void setLeaveRequest(LeaveRequest leaveRequest) {
        this.leaveRequest = leaveRequest;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public Employee getPerformedBy() {
        return performedBy;
    }
    
    public void setPerformedBy(Employee performedBy) {
        this.performedBy = performedBy;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public String getOldStatus() {
        return oldStatus;
    }
    
    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public Integer getLeaveDaysAffected() {
        return leaveDaysAffected;
    }
    
    public void setLeaveDaysAffected(Integer leaveDaysAffected) {
        this.leaveDaysAffected = leaveDaysAffected;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}