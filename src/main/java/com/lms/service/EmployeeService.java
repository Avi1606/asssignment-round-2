package com.lms.service;

import com.lms.dto.EmployeeRequest;
import com.lms.dto.EmployeeResponse;
import com.lms.exception.BusinessRuleException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.Department;
import com.lms.model.Employee;
import com.lms.repository.DepartmentRepository;
import com.lms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Employee management operations
 */
@Service
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, 
                          DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }
    
    /**
     * Create a new employee
     * @param request the employee creation request
     * @return the created employee response
     */
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Validate unique constraints
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BusinessRuleException("Employee ID already exists: " + request.getEmployeeId());
        }
        
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already exists: " + request.getEmail());
        }
        
        // Find department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        
        // Find manager if specified
        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", request.getManagerId()));
        }
        
        // Create employee
        Employee employee = new Employee(
                request.getEmployeeId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getJoiningDate(),
                department
        );
        
        employee.setManager(manager);
        employee.setAnnualLeaveBalance(request.getAnnualLeaveBalance());
        employee.setSickLeaveBalance(request.getSickLeaveBalance());
        
        Employee savedEmployee = employeeRepository.save(employee);
        return new EmployeeResponse(savedEmployee);
    }
    
    /**
     * Get all employees
     * @return list of employee responses
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employee by ID
     * @param id the employee ID
     * @return the employee response
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return new EmployeeResponse(employee);
    }
    
    /**
     * Get employee by employee ID
     * @param employeeId the employee ID string
     * @return the employee response
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return new EmployeeResponse(employee);
    }
    
    /**
     * Update employee
     * @param id the employee ID
     * @param request the employee update request
     * @return the updated employee response
     */
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Check for unique constraints if values are changing
        if (!employee.getEmployeeId().equals(request.getEmployeeId()) &&
            employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BusinessRuleException("Employee ID already exists: " + request.getEmployeeId());
        }
        
        if (!employee.getEmail().equals(request.getEmail()) &&
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already exists: " + request.getEmail());
        }
        
        // Find department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        
        // Find manager if specified
        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", request.getManagerId()));
            
            // Check if employee is trying to be their own manager
            if (manager.getId().equals(id)) {
                throw new BusinessRuleException("Employee cannot be their own manager");
            }
        }
        
        // Update employee fields
        employee.setEmployeeId(request.getEmployeeId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setDepartment(department);
        employee.setManager(manager);
        employee.setAnnualLeaveBalance(request.getAnnualLeaveBalance());
        employee.setSickLeaveBalance(request.getSickLeaveBalance());
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return new EmployeeResponse(updatedEmployee);
    }
    
    /**
     * Delete employee
     * @param id the employee ID
     */
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Check if employee has subordinates
        List<Employee> subordinates = employeeRepository.findByManagerId(id);
        if (!subordinates.isEmpty()) {
            throw new BusinessRuleException("Cannot delete employee who has subordinates. " +
                    "Please reassign subordinates first.");
        }
        
        // Set status to terminated instead of hard delete to maintain data integrity
        employee.setStatus(Employee.EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
    }
    
    /**
     * Get employees by department
     * @param departmentId the department ID
     * @return list of employee responses
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees by manager
     * @param managerId the manager ID
     * @return list of employee responses
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByManager(Long managerId) {
        return employeeRepository.findByManagerId(managerId)
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees with low leave balance
     * @param threshold the balance threshold
     * @return list of employee responses
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesWithLowLeaveBalance(Integer threshold) {
        return employeeRepository.findEmployeesWithLowLeaveBalance(threshold)
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }
}