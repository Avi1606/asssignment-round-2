package com.lms.controller;

import com.lms.dto.DepartmentDto;
import com.lms.service.DepartmentService;
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
 * REST Controller for Department operations
 * 
 * @author Avi Patel
 */
@RestController
@RequestMapping("/departments")
@Tag(name = "Department Management", description = "APIs for managing organizational departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Operation(summary = "Create a new department")
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all departments")
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @Operation(summary = "Get department by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(
            @Parameter(description = "Department ID") @PathVariable Long id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @Operation(summary = "Update department")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(
            @Parameter(description = "Department ID") @PathVariable Long id,
            @Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updated = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete department")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @Parameter(description = "Department ID") @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search departments by name")
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentDto>> searchDepartments(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        List<DepartmentDto> departments = departmentService.searchDepartmentsByName(searchTerm);
        return ResponseEntity.ok(departments);
    }

    @Operation(summary = "Get departments with employees")
    @GetMapping("/with-employees")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsWithEmployees() {
        List<DepartmentDto> departments = departmentService.getDepartmentsWithEmployees();
        return ResponseEntity.ok(departments);
    }
}