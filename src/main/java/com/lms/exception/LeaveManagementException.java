package com.lms.exception;

/**
 * Custom exception for leave management business logic violations
 * 
 * @author Avi Patel
 */
public class LeaveManagementException extends RuntimeException {

    private final String errorCode;

    public LeaveManagementException(String message) {
        super(message);
        this.errorCode = "LEAVE_MANAGEMENT_ERROR";
    }

    public LeaveManagementException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public LeaveManagementException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "LEAVE_MANAGEMENT_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}