package com.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Leave transaction entity for audit trail
 * 
 * @author Avi Patel
 */
@Entity
@Table(name = "leave_transactions")
public class LeaveTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    private LeaveRequest leaveRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private Employee performedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private LeaveStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private LeaveStatus newStatus;

    @Column(name = "old_balance")
    private Integer oldBalance;

    @Column(name = "new_balance")
    private Integer newBalance;

    @Column(name = "days_adjusted")
    private Integer daysAdjusted;

    private String comments;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    // Constructors
    public LeaveTransaction() {
        this.transactionDate = LocalDateTime.now();
    }

    public LeaveTransaction(LeaveRequest leaveRequest, Employee performedBy, 
                          TransactionType transactionType, String comments) {
        this();
        this.leaveRequest = leaveRequest;
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

    public LeaveStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(LeaveStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public LeaveStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(LeaveStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Integer getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(Integer oldBalance) {
        this.oldBalance = oldBalance;
    }

    public Integer getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Integer newBalance) {
        this.newBalance = newBalance;
    }

    public Integer getDaysAdjusted() {
        return daysAdjusted;
    }

    public void setDaysAdjusted(Integer daysAdjusted) {
        this.daysAdjusted = daysAdjusted;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "LeaveTransaction{" +
                "id=" + id +
                ", transactionType=" + transactionType +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                ", transactionDate=" + transactionDate +
                '}';
    }
}