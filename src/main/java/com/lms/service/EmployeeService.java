package com.lms.service;

import com.lms.dto.EmployeeDto;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.Department;
import com.lms.model.Employee;
import com.lms.model.EmployeeRole;
import com.lms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Employee operations
 * 
 * @author Avi Patel
 */
@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
    }

    /**
     * Create a new employee
     */
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        // Validate unique constraints
        if (employeeRepository.existsByEmployeeId(employeeDto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee with ID '" + employeeDto.getEmployeeId() + "' already exists");
        }
        
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new IllegalArgumentException("Employee with email '" + employeeDto.getEmail() + "' already exists");
        }

        // Get department
        Department department = departmentService.getDepartmentEntity(employeeDto.getDepartmentId());

        // Create employee entity
        Employee employee = new Employee(
                employeeDto.getEmployeeId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail(),
                employeeDto.getPhoneNumber(),
                employeeDto.getJoiningDate(),
                department
        );

        employee.setRole(employeeDto.getRole());

        // Set manager if provided
        if (employeeDto.getManagerId() != null) {
            Employee manager = getEmployeeEntity(employeeDto.getManagerId());
            employee.setManager(manager);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    /**
     * Get all employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findByActiveTrue();
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get employee by ID
     */
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return convertToDto(employee);
    }

    /**
     * Get employee by employee ID
     */
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return convertToDto(employee);
    }

    /**
     * Update employee
     */
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee existingEmployee = getEmployeeEntity(id);

        // Check unique constraints (excluding current employee)
        if (!existingEmployee.getEmployeeId().equals(employeeDto.getEmployeeId()) && 
            employeeRepository.existsByEmployeeId(employeeDto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee with ID '" + employeeDto.getEmployeeId() + "' already exists");
        }

        if (!existingEmployee.getEmail().equals(employeeDto.getEmail()) && 
            employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new IllegalArgumentException("Employee with email '" + employeeDto.getEmail() + "' already exists");
        }

        // Update fields
        existingEmployee.setEmployeeId(employeeDto.getEmployeeId());
        existingEmployee.setFirstName(employeeDto.getFirstName());
        existingEmployee.setLastName(employeeDto.getLastName());
        existingEmployee.setEmail(employeeDto.getEmail());
        existingEmployee.setPhoneNumber(employeeDto.getPhoneNumber());
        existingEmployee.setJoiningDate(employeeDto.getJoiningDate());
        existingEmployee.setRole(employeeDto.getRole());

        // Update department if changed
        if (!existingEmployee.getDepartment().getId().equals(employeeDto.getDepartmentId())) {
            Department department = departmentService.getDepartmentEntity(employeeDto.getDepartmentId());
            existingEmployee.setDepartment(department);
        }

        // Update manager if changed
        if (employeeDto.getManagerId() != null) {
            if (existingEmployee.getManager() == null || 
                !existingEmployee.getManager().getId().equals(employeeDto.getManagerId())) {
                Employee manager = getEmployeeEntity(employeeDto.getManagerId());
                existingEmployee.setManager(manager);
            }
        } else {
            existingEmployee.setManager(null);
        }

        // Update leave balances if provided
        if (employeeDto.getAnnualLeaveBalance() != null) {
            existingEmployee.setAnnualLeaveBalance(employeeDto.getAnnualLeaveBalance());
        }
        if (employeeDto.getSickLeaveBalance() != null) {
            existingEmployee.setSickLeaveBalance(employeeDto.getSickLeaveBalance());
        }
        if (employeeDto.getPersonalLeaveBalance() != null) {
            existingEmployee.setPersonalLeaveBalance(employeeDto.getPersonalLeaveBalance());
        }

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return convertToDto(updatedEmployee);
    }

    /**
     * Delete employee (soft delete by setting active = false)
     */
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeEntity(id);
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    /**
     * Get employees by department
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartment(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream()
                .filter(Employee::getActive)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get employees by manager
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByManager(Long managerId) {
        List<Employee> employees = employeeRepository.findActiveSubordinates(managerId);
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get employees by role
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByRole(EmployeeRole role) {
        List<Employee> employees = employeeRepository.findByRole(role);
        return employees.stream()
                .filter(Employee::getActive)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Search employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> searchEmployees(String searchTerm) {
        List<Employee> employees = employeeRepository.searchActiveEmployees(searchTerm);
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Update employee leave balances
     */
    public EmployeeDto updateLeaveBalances(Long employeeId, Integer annualLeave, 
                                         Integer sickLeave, Integer personalLeave) {
        Employee employee = getEmployeeEntity(employeeId);
        
        if (annualLeave != null) {
            employee.setAnnualLeaveBalance(annualLeave);
        }
        if (sickLeave != null) {
            employee.setSickLeaveBalance(sickLeave);
        }
        if (personalLeave != null) {
            employee.setPersonalLeaveBalance(personalLeave);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDto(updatedEmployee);
    }

    // Helper method to convert entity to DTO
    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setDepartmentId(employee.getDepartment().getId());
        dto.setDepartmentName(employee.getDepartment().getName());
        dto.setRole(employee.getRole());
        dto.setAnnualLeaveBalance(employee.getAnnualLeaveBalance());
        dto.setSickLeaveBalance(employee.getSickLeaveBalance());
        dto.setPersonalLeaveBalance(employee.getPersonalLeaveBalance());
        dto.setActive(employee.getActive());

        if (employee.getManager() != null) {
            dto.setManagerId(employee.getManager().getId());
            dto.setManagerName(employee.getManager().getFullName());
        }

        return dto;
    }

    // Helper method to get entity (used by other services)
    @Transactional(readOnly = true)
    public Employee getEmployeeEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }
}