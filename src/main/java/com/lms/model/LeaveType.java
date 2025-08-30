package com.lms.model;

/**
 * Leave type enumeration
 * 
 * @author Avi Patel
 */
public enum LeaveType {
    ANNUAL("Annual Leave"),
    SICK("Sick Leave"),
    PERSONAL("Personal Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    EMERGENCY("Emergency Leave");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}