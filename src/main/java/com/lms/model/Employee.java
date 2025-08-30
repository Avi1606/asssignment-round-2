package com.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Employee entity representing an employee in the system
 */
@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Employee ID is required")
    @Size(max = 50)
    private String employeeId;
    
    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;
    
    @Column(nullable = false)
    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column(nullable = false)
    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Department is required")
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;
    
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Employee> subordinates;
    
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LeaveRequest> leaveRequests;
    
    @Column(name = "annual_leave_balance", nullable = false)
    private Integer annualLeaveBalance = 20; // Default 20 days per year
    
    @Column(name = "sick_leave_balance", nullable = false)
    private Integer sickLeaveBalance = 10; // Default 10 days per year
    
    @Column(name = "used_annual_leave", nullable = false)
    private Integer usedAnnualLeave = 0;
    
    @Column(name = "used_sick_leave", nullable = false)
    private Integer usedSickLeave = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum EmployeeStatus {
        ACTIVE, INACTIVE, TERMINATED
    }
    
    // Constructors
    public Employee() {}
    
    public Employee(String employeeId, String firstName, String lastName, String email, 
                   LocalDate joiningDate, Department department) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joiningDate = joiningDate;
        this.department = department;
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
        return firstName + " " + lastName;
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
    
    public Department getDepartment() {
        return department;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }
    
    public Employee getManager() {
        return manager;
    }
    
    public void setManager(Employee manager) {
        this.manager = manager;
    }
    
    public List<Employee> getSubordinates() {
        return subordinates;
    }
    
    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }
    
    public List<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }
    
    public void setLeaveRequests(List<LeaveRequest> leaveRequests) {
        this.leaveRequests = leaveRequests;
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
        return annualLeaveBalance - usedAnnualLeave;
    }
    
    public Integer getAvailableSickLeave() {
        return sickLeaveBalance - usedSickLeave;
    }
    
    public EmployeeStatus getStatus() {
        return status;
    }
    
    public void setStatus(EmployeeStatus status) {
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