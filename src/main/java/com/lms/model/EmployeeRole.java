package com.lms.model;

/**
 * Employee role enumeration
 * 
 * @author Avi Patel
 */
public enum EmployeeRole {
    EMPLOYEE("Employee"),
    MANAGER("Manager"),
    HR_ADMIN("HR Administrator");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}