package com.lms.model;

/**
 * Transaction type enumeration for audit trail
 * 
 * @author Avi Patel
 */
public enum TransactionType {
    LEAVE_APPLIED("Leave Applied"),
    LEAVE_APPROVED("Leave Approved"),
    LEAVE_REJECTED("Leave Rejected"),
    LEAVE_CANCELLED("Leave Cancelled"),
    BALANCE_ADJUSTED("Balance Adjusted"),
    BALANCE_CREDITED("Balance Credited");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}