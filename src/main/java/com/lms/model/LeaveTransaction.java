package com.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * LeaveTransaction entity for maintaining audit trail
 * 
 * Records all changes made to leave requests including
 * status changes, balance updates, and system actions.
 */
@Entity
@Table(name = "leave_transactions", indexes = {
    @Index(name = "idx_transaction_leave", columnList = "leave_request_id"),
    @Index(name = "idx_transaction_type", columnList = "transaction_type"),
    @Index(name = "idx_transaction_date", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class LeaveTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "balance_before")
    private Integer balanceBefore;

    @Column(name = "balance_after")
    private Integer balanceAfter;

    @Column(name = "days_affected")
    private Integer daysAffected;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    @NotNull(message = "Leave request is required")
    private LeaveRequest leaveRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private Employee performedBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public LeaveTransaction() {}

    public LeaveTransaction(TransactionType transactionType, String description, 
                           LeaveRequest leaveRequest, Employee performedBy) {
        this.transactionType = transactionType;
        this.description = description;
        this.leaveRequest = leaveRequest;
        this.performedBy = performedBy;
    }

    public LeaveTransaction(TransactionType transactionType, String description,
                           Integer balanceBefore, Integer balanceAfter, Integer daysAffected,
                           LeaveRequest leaveRequest, Employee performedBy) {
        this.transactionType = transactionType;
        this.description = description;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.daysAffected = daysAffected;
        this.leaveRequest = leaveRequest;
        this.performedBy = performedBy;
    }

    // Transaction types enum
    public enum TransactionType {
        LEAVE_APPLIED("Leave Application Submitted"),
        LEAVE_APPROVED("Leave Request Approved"),
        LEAVE_REJECTED("Leave Request Rejected"),
        LEAVE_CANCELLED("Leave Request Cancelled"),
        BALANCE_DEDUCTED("Leave Balance Deducted"),
        BALANCE_RESTORED("Leave Balance Restored"),
        BALANCE_ADJUSTED("Leave Balance Adjusted"),
        SYSTEM_UPDATE("System Update");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Business methods
    public boolean isBalanceTransaction() {
        return transactionType == TransactionType.BALANCE_DEDUCTED || 
               transactionType == TransactionType.BALANCE_RESTORED ||
               transactionType == TransactionType.BALANCE_ADJUSTED;
    }

    public boolean isStatusTransaction() {
        return transactionType == TransactionType.LEAVE_APPLIED ||
               transactionType == TransactionType.LEAVE_APPROVED ||
               transactionType == TransactionType.LEAVE_REJECTED ||
               transactionType == TransactionType.LEAVE_CANCELLED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Integer balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public Integer getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Integer balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Integer getDaysAffected() {
        return daysAffected;
    }

    public void setDaysAffected(Integer daysAffected) {
        this.daysAffected = daysAffected;
    }

    public LeaveRequest getLeaveRequest() {
        return leaveRequest;
    }

    public void setLeaveRequest(LeaveRequest leaveRequest) {
        this.leaveRequest = leaveRequest;
    }

    public Employee getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Employee performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LeaveTransaction{" +
                "id=" + id +
                ", transactionType=" + transactionType +
                ", description='" + description + '\'' +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", daysAffected=" + daysAffected +
                ", performedBy=" + (performedBy != null ? performedBy.getName() : "System") +
                ", createdAt=" + createdAt +
                '}';
    }
}