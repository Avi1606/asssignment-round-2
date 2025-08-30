package com.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for Employee creation requests
 */
public class EmployeeCreateDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private Long managerId;

    // Constructors
    public EmployeeCreateDTO() {}

    public EmployeeCreateDTO(String name, String email, LocalDate joiningDate, Long departmentId, Long managerId) {
        this.name = name;
        this.email = email;
        this.joiningDate = joiningDate;
        this.departmentId = departmentId;
        this.managerId = managerId;
    }

    // Getters and Setters
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() {
        return "EmployeeCreateDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", joiningDate=" + joiningDate +
                ", departmentId=" + departmentId +
                ", managerId=" + managerId +
                '}';
    }
}