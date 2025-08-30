package com.lms.controller;

import com.lms.dto.EmployeeRequest;
import com.lms.dto.EmployeeResponse;
import com.lms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Employee management operations
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    /**
     * Create a new employee
     */
    @PostMapping
    @Operation(summary = "Create Employee", description = "Create a new employee in the system")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get all employees
     */
    @GetMapping
    @Operation(summary = "Get All Employees", description = "Retrieve all employees in the system")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employee by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Employee by ID", description = "Retrieve a specific employee by their ID")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * Get employee by employee ID
     */
    @GetMapping("/employee-id/{employeeId}")
    @Operation(summary = "Get Employee by Employee ID", description = "Retrieve a specific employee by their employee ID")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        EmployeeResponse employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * Update employee
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Employee", description = "Update an existing employee")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, 
                                                          @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete employee
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Employee", description = "Delete an employee (marks as terminated)")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get employees by department
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get Employees by Department", description = "Retrieve all employees in a specific department")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees by manager
     */
    @GetMapping("/manager/{managerId}")
    @Operation(summary = "Get Employees by Manager", description = "Retrieve all employees under a specific manager")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByManager(@PathVariable Long managerId) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees with low leave balance
     */
    @GetMapping("/low-balance")
    @Operation(summary = "Get Employees with Low Leave Balance", description = "Retrieve employees with leave balance below threshold")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesWithLowLeaveBalance(
            @RequestParam(defaultValue = "5") Integer threshold) {
        List<EmployeeResponse> employees = employeeService.getEmployeesWithLowLeaveBalance(threshold);
        return ResponseEntity.ok(employees);
    }
}