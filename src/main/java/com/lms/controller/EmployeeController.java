package com.lms.controller;

import com.lms.dto.EmployeeDto;
import com.lms.model.EmployeeRole;
import com.lms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Employee operations
 * 
 * @author Avi Patel
 */
@RestController
@RequestMapping("/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create a new employee")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto created = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all employees")
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employee by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(
            @Parameter(description = "Employee ID") @PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Get employee by Employee ID")
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeByEmployeeId(
            @Parameter(description = "Employee ID (string)") @PathVariable String employeeId) {
        EmployeeDto employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Update employee")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long id,
            @Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto updated = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete employee (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get employees by department")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(
            @Parameter(description = "Department ID") @PathVariable Long departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by manager")
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByManager(
            @Parameter(description = "Manager ID") @PathVariable Long managerId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by role")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByRole(
            @Parameter(description = "Employee role") @PathVariable EmployeeRole role) {
        List<EmployeeDto> employees = employeeService.getEmployeesByRole(role);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Search employees")
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        List<EmployeeDto> employees = employeeService.searchEmployees(searchTerm);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Update employee leave balances")
    @PatchMapping("/{employeeId}/leave-balances")
    public ResponseEntity<EmployeeDto> updateLeaveBalances(
            @Parameter(description = "Employee ID") @PathVariable Long employeeId,
            @Parameter(description = "Annual leave balance") @RequestParam(required = false) Integer annualLeave,
            @Parameter(description = "Sick leave balance") @RequestParam(required = false) Integer sickLeave,
            @Parameter(description = "Personal leave balance") @RequestParam(required = false) Integer personalLeave) {
        
        EmployeeDto updated = employeeService.updateLeaveBalances(employeeId, annualLeave, sickLeave, personalLeave);
        return ResponseEntity.ok(updated);
    }
}