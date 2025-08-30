package com.lms.service;

import com.lms.dto.EmployeeCreateDTO;
import com.lms.dto.EmployeeResponseDTO;
import com.lms.exception.ResourceNotFoundException;
import com.lms.exception.BusinessException;
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
 * Service class for Employee business logic operations
 * 
 * Handles all employee-related operations including creation,
 * updates, validation, and business rule enforcement.
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Create a new employee
     */
    public EmployeeResponseDTO createEmployee(EmployeeCreateDTO createDTO) {
        // Validate email uniqueness
        if (employeeRepository.existsByEmail(createDTO.getEmail())) {
            throw new BusinessException("Email already exists: " + createDTO.getEmail());
        }

        // Validate department exists
        Department department = departmentRepository.findById(createDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + createDTO.getDepartmentId()));

        // Validate manager if provided
        Employee manager = null;
        if (createDTO.getManagerId() != null) {
            manager = employeeRepository.findById(createDTO.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + createDTO.getManagerId()));
            
            // Ensure manager is active
            if (!manager.getIsActive()) {
                throw new BusinessException("Cannot assign inactive manager");
            }
        }

        // Create employee entity
        Employee employee = new Employee(
                createDTO.getName(),
                createDTO.getEmail(),
                createDTO.getJoiningDate(),
                department
        );
        employee.setManager(manager);

        // Save and return response
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponseDTO(savedEmployee);
    }

    /**
     * Get all employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAllWithDetails()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get employee by ID
     */
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return mapToResponseDTO(employee);
    }

    /**
     * Update employee
     */
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeCreateDTO updateDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // Check if email is being changed and if new email already exists
        if (!existingEmployee.getEmail().equals(updateDTO.getEmail()) &&
            employeeRepository.existsByEmail(updateDTO.getEmail())) {
            throw new BusinessException("Email already exists: " + updateDTO.getEmail());
        }

        // Validate department
        Department department = departmentRepository.findById(updateDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + updateDTO.getDepartmentId()));

        // Validate manager if provided
        Employee manager = null;
        if (updateDTO.getManagerId() != null) {
            // Prevent self-management
            if (updateDTO.getManagerId().equals(id)) {
                throw new BusinessException("Employee cannot be their own manager");
            }

            manager = employeeRepository.findById(updateDTO.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + updateDTO.getManagerId()));

            if (!manager.getIsActive()) {
                throw new BusinessException("Cannot assign inactive manager");
            }
        }

        // Update employee
        existingEmployee.setName(updateDTO.getName());
        existingEmployee.setEmail(updateDTO.getEmail());
        existingEmployee.setJoiningDate(updateDTO.getJoiningDate());
        existingEmployee.setDepartment(department);
        existingEmployee.setManager(manager);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return mapToResponseDTO(updatedEmployee);
    }

    /**
     * Delete (deactivate) employee
     */
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // Check if employee has pending leave requests
        // This would require LeaveRequestRepository, but for now we'll just deactivate
        
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    /**
     * Get employees by department
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentIdAndIsActiveTrue(departmentId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get employees by manager
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getEmployeesByManager(Long managerId) {
        return employeeRepository.findByManagerIdAndIsActiveTrue(managerId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update employee leave balances
     */
    public void updateLeaveBalance(Long employeeId, String leaveType, int days) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        switch (leaveType.toUpperCase()) {
            case "ANNUAL":
                employee.setAnnualLeaveBalance(Math.max(0, employee.getAnnualLeaveBalance() + days));
                break;
            case "SICK":
                employee.setSickLeaveBalance(Math.max(0, employee.getSickLeaveBalance() + days));
                break;
            case "CASUAL":
                employee.setCasualLeaveBalance(Math.max(0, employee.getCasualLeaveBalance() + days));
                break;
            default:
                throw new BusinessException("Invalid leave type: " + leaveType);
        }

        employeeRepository.save(employee);
    }

    /**
     * Map Employee entity to EmployeeResponseDTO
     */
    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setIsActive(employee.getIsActive());
        dto.setAnnualLeaveBalance(employee.getAnnualLeaveBalance());
        dto.setSickLeaveBalance(employee.getSickLeaveBalance());
        dto.setCasualLeaveBalance(employee.getCasualLeaveBalance());
        dto.setTotalLeaveBalance(employee.getTotalLeaveBalance());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());

        // Set department info
        if (employee.getDepartment() != null) {
            EmployeeResponseDTO.DepartmentResponseDTO deptDTO = new EmployeeResponseDTO.DepartmentResponseDTO(
                    employee.getDepartment().getId(),
                    employee.getDepartment().getName(),
                    employee.getDepartment().getDescription()
            );
            dto.setDepartment(deptDTO);
        }

        // Set manager info
        if (employee.getManager() != null) {
            EmployeeResponseDTO.ManagerResponseDTO managerDTO = new EmployeeResponseDTO.ManagerResponseDTO(
                    employee.getManager().getId(),
                    employee.getManager().getName(),
                    employee.getManager().getEmail()
            );
            dto.setManager(managerDTO);
        }

        return dto;
    }
}