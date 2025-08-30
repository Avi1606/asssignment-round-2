package com.lms.service;

import com.lms.dto.DepartmentDto;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.Department;
import com.lms.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Department operations
 * 
 * @author Avi Patel
 */
@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Create a new department
     */
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        // Check if department name already exists
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new IllegalArgumentException("Department with name '" + departmentDto.getName() + "' already exists");
        }

        Department department = new Department(departmentDto.getName(), departmentDto.getDescription());
        Department savedDepartment = departmentRepository.save(department);
        
        return convertToDto(savedDepartment);
    }

    /**
     * Get all departments
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get department by ID
     */
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return convertToDto(department);
    }

    /**
     * Get department by name
     */
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentByName(String name) {
        Department department = departmentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "name", name));
        return convertToDto(department);
    }

    /**
     * Update department
     */
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        // Check if new name conflicts with existing departments (excluding current)
        if (!existingDepartment.getName().equals(departmentDto.getName()) && 
            departmentRepository.existsByName(departmentDto.getName())) {
            throw new IllegalArgumentException("Department with name '" + departmentDto.getName() + "' already exists");
        }

        existingDepartment.setName(departmentDto.getName());
        existingDepartment.setDescription(departmentDto.getDescription());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return convertToDto(updatedDepartment);
    }

    /**
     * Delete department
     */
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        // Check if department has employees
        if (!department.getEmployees().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete department that has employees. Please reassign employees first.");
        }

        departmentRepository.delete(department);
    }

    /**
     * Search departments by name
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> searchDepartmentsByName(String searchTerm) {
        List<Department> departments = departmentRepository.findByNameContainingIgnoreCase(searchTerm);
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get departments with employees
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartmentsWithEmployees() {
        List<Department> departments = departmentRepository.findDepartmentsWithEmployees();
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert entity to DTO
    private DepartmentDto convertToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setEmployeeCount((long) department.getEmployees().size());
        return dto;
    }

    // Helper method to get entity (used by other services)
    @Transactional(readOnly = true)
    public Department getDepartmentEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
}