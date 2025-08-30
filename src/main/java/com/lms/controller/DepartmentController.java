package com.lms.controller;

import com.lms.model.Department;
import com.lms.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Department management operations
 */
@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department Management", description = "APIs for managing departments")
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
    
    /**
     * Create a new department
     */
    @PostMapping
    @Operation(summary = "Create Department", description = "Create a new department")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody Department department) {
        Department createdDepartment = departmentService.createDepartment(department);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }
    
    /**
     * Get all departments
     */
    @GetMapping
    @Operation(summary = "Get All Departments", description = "Retrieve all departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get department by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Department by ID", description = "Retrieve a specific department by its ID")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }
    
    /**
     * Get department by name
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "Get Department by Name", description = "Retrieve a specific department by its name")
    public ResponseEntity<Department> getDepartmentByName(@PathVariable String name) {
        Department department = departmentService.getDepartmentByName(name);
        return ResponseEntity.ok(department);
    }
    
    /**
     * Update department
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Department", description = "Update an existing department")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, 
                                                      @Valid @RequestBody Department department) {
        Department updatedDepartment = departmentService.updateDepartment(id, department);
        return ResponseEntity.ok(updatedDepartment);
    }
    
    /**
     * Delete department
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Department", description = "Delete a department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}