package com.lms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Employee response data
 */
public class EmployeeResponseDTO {

    private Long id;
    private String name;
    private String email;
    private LocalDate joiningDate;
    private Boolean isActive;
    private Integer annualLeaveBalance;
    private Integer sickLeaveBalance;
    private Integer casualLeaveBalance;
    private Integer totalLeaveBalance;
    private DepartmentResponseDTO department;
    private ManagerResponseDTO manager;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public EmployeeResponseDTO() {}

    // Static nested class for manager information
    public static class ManagerResponseDTO {
        private Long id;
        private String name;
        private String email;

        public ManagerResponseDTO() {}

        public ManagerResponseDTO(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class DepartmentResponseDTO {
        private Long id;
        private String name;
        private String description;

        public DepartmentResponseDTO() {}

        public DepartmentResponseDTO(Long id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getAnnualLeaveBalance() {
        return annualLeaveBalance;
    }

    public void setAnnualLeaveBalance(Integer annualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
    }

    public Integer getSickLeaveBalance() {
        return sickLeaveBalance;
    }

    public void setSickLeaveBalance(Integer sickLeaveBalance) {
        this.sickLeaveBalance = sickLeaveBalance;
    }

    public Integer getCasualLeaveBalance() {
        return casualLeaveBalance;
    }

    public void setCasualLeaveBalance(Integer casualLeaveBalance) {
        this.casualLeaveBalance = casualLeaveBalance;
    }

    public Integer getTotalLeaveBalance() {
        return totalLeaveBalance;
    }

    public void setTotalLeaveBalance(Integer totalLeaveBalance) {
        this.totalLeaveBalance = totalLeaveBalance;
    }

    public DepartmentResponseDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentResponseDTO department) {
        this.department = department;
    }

    public ManagerResponseDTO getManager() {
        return manager;
    }

    public void setManager(ManagerResponseDTO manager) {
        this.manager = manager;
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