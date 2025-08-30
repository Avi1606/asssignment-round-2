package com.lms.dto;

import com.lms.model.Employee;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Employee responses
 */
public class EmployeeResponse {
    
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private LocalDate joiningDate;
    private String departmentName;
    private Long departmentId;
    private String managerName;
    private Long managerId;
    private Integer annualLeaveBalance;
    private Integer sickLeaveBalance;
    private Integer usedAnnualLeave;
    private Integer usedSickLeave;
    private Integer availableAnnualLeave;
    private Integer availableSickLeave;
    private Employee.EmployeeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public EmployeeResponse() {}
    
    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.employeeId = employee.getEmployeeId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.fullName = employee.getFullName();
        this.email = employee.getEmail();
        this.joiningDate = employee.getJoiningDate();
        this.departmentName = employee.getDepartment() != null ? employee.getDepartment().getName() : null;
        this.departmentId = employee.getDepartment() != null ? employee.getDepartment().getId() : null;
        this.managerName = employee.getManager() != null ? employee.getManager().getFullName() : null;
        this.managerId = employee.getManager() != null ? employee.getManager().getId() : null;
        this.annualLeaveBalance = employee.getAnnualLeaveBalance();
        this.sickLeaveBalance = employee.getSickLeaveBalance();
        this.usedAnnualLeave = employee.getUsedAnnualLeave();
        this.usedSickLeave = employee.getUsedSickLeave();
        this.availableAnnualLeave = employee.getAvailableAnnualLeave();
        this.availableSickLeave = employee.getAvailableSickLeave();
        this.status = employee.getStatus();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public Long getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public Long getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
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
    
    public Integer getUsedAnnualLeave() {
        return usedAnnualLeave;
    }
    
    public void setUsedAnnualLeave(Integer usedAnnualLeave) {
        this.usedAnnualLeave = usedAnnualLeave;
    }
    
    public Integer getUsedSickLeave() {
        return usedSickLeave;
    }
    
    public void setUsedSickLeave(Integer usedSickLeave) {
        this.usedSickLeave = usedSickLeave;
    }
    
    public Integer getAvailableAnnualLeave() {
        return availableAnnualLeave;
    }
    
    public void setAvailableAnnualLeave(Integer availableAnnualLeave) {
        this.availableAnnualLeave = availableAnnualLeave;
    }
    
    public Integer getAvailableSickLeave() {
        return availableSickLeave;
    }
    
    public void setAvailableSickLeave(Integer availableSickLeave) {
        this.availableSickLeave = availableSickLeave;
    }
    
    public Employee.EmployeeStatus getStatus() {
        return status;
    }
    
    public void setStatus(Employee.EmployeeStatus status) {
        this.status = status;
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