package com.lms.controller;

import com.lms.dto.EmployeeCreateDTO;
import com.lms.dto.EmployeeResponseDTO;
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
 * REST controller for Employee management operations
 * 
 * Provides endpoints for CRUD operations on employees,
 * department assignments, and manager relationships.
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing employee data")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Create a new employee", description = "Creates a new employee with department and manager assignment")
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeCreateDTO createDTO) {
        EmployeeResponseDTO createdEmployee = employeeService.createEmployee(createDTO);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all employees", description = "Retrieves all active employees with their details")
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employee by ID", description = "Retrieves a specific employee by their ID")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Update employee", description = "Updates an existing employee's information")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeCreateDTO updateDTO) {
        EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(id, updateDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Operation(summary = "Delete employee", description = "Deactivates an employee (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get employees by department", description = "Retrieves all employees in a specific department")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<EmployeeResponseDTO> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by manager", description = "Retrieves all employees under a specific manager")
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByManager(@PathVariable Long managerId) {
        List<EmployeeResponseDTO> employees = employeeService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Update leave balance", description = "Updates an employee's leave balance")
    @PostMapping("/{id}/leave-balance")
    public ResponseEntity<Void> updateLeaveBalance(
            @PathVariable Long id,
            @RequestParam String leaveType,
            @RequestParam int days) {
        employeeService.updateLeaveBalance(id, leaveType, days);
        return ResponseEntity.ok().build();
    }
}