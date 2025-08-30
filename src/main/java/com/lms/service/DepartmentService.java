package com.lms.service;

import com.lms.model.Department;
import com.lms.repository.DepartmentRepository;
import com.lms.repository.EmployeeRepository;
import com.lms.exception.BusinessRuleException;
import com.lms.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Department management operations
 */
@Service
@Transactional
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    
    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository,
                           EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }
    
    /**
     * Create a new department
     * @param department the department to create
     * @return the created department
     */
    public Department createDepartment(Department department) {
        // Validate unique name
        if (departmentRepository.existsByName(department.getName())) {
            throw new BusinessRuleException("Department name already exists: " + department.getName());
        }
        
        return departmentRepository.save(department);
    }
    
    /**
     * Get all departments
     * @return list of all departments
     */
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    /**
     * Get department by ID
     * @param id the department ID
     * @return the department
     */
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
    
    /**
     * Get department by name
     * @param name the department name
     * @return the department
     */
    @Transactional(readOnly = true)
    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "name", name));
    }
    
    /**
     * Update department
     * @param id the department ID
     * @param department the updated department data
     * @return the updated department
     */
    public Department updateDepartment(Long id, Department department) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        
        // Check unique name if it's being changed
        if (!existingDepartment.getName().equals(department.getName()) &&
            departmentRepository.existsByName(department.getName())) {
            throw new BusinessRuleException("Department name already exists: " + department.getName());
        }
        
        existingDepartment.setName(department.getName());
        existingDepartment.setDescription(department.getDescription());
        
        return departmentRepository.save(existingDepartment);
    }
    
    /**
     * Delete department
     * @param id the department ID
     */
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        
        // Check if department has employees
        long employeeCount = employeeRepository.countByDepartmentId(id);
        if (employeeCount > 0) {
            throw new BusinessRuleException("Cannot delete department with existing employees. " +
                    "Please reassign employees first.");
        }
        
        departmentRepository.delete(department);
    }
}