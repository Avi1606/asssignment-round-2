package com.lms.service;

import com.lms.dto.EmployeeCreateDTO;
import com.lms.dto.EmployeeResponseDTO;
import com.lms.exception.BusinessException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.Department;
import com.lms.model.Employee;
import com.lms.repository.DepartmentRepository;
import com.lms.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeService
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Department testDepartment;
    private Employee testEmployee;
    private EmployeeCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Engineering", "Software Development");
        testDepartment.setId(1L);

        testEmployee = new Employee("Test Employee", "test@company.com", 
                                  LocalDate.of(2023, 1, 15), testDepartment);
        testEmployee.setId(1L);

        createDTO = new EmployeeCreateDTO("New Employee", "new@company.com", 
                                        LocalDate.of(2023, 6, 1), 1L, null);
    }

    @Test
    void createEmployee_Success() {
        // Arrange
        when(employeeRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // Act
        EmployeeResponseDTO result = employeeService.createEmployee(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Employee", result.getName());
        assertEquals("test@company.com", result.getEmail());
        assertEquals("Engineering", result.getDepartment().getName());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_EmailAlreadyExists() {
        // Arrange
        when(employeeRepository.existsByEmail(createDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> employeeService.createEmployee(createDTO));
        assertEquals("Email already exists: new@company.com", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_DepartmentNotFound() {
        // Arrange
        when(employeeRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> employeeService.createEmployee(createDTO));
        assertEquals("Department not found with ID: 1", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_Success() {
        // Arrange
        when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testEmployee));

        // Act
        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Employee", result.getName());
    }

    @Test
    void getEmployeeById_NotFound() {
        // Arrange
        when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> employeeService.getEmployeeById(1L));
        assertEquals("Employee not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateLeaveBalance_Success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // Act
        employeeService.updateLeaveBalance(1L, "ANNUAL", -5);

        // Assert
        verify(employeeRepository).save(testEmployee);
        assertEquals(16, testEmployee.getAnnualLeaveBalance()); // 21 - 5
    }

    @Test
    void updateLeaveBalance_InvalidLeaveType() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> employeeService.updateLeaveBalance(1L, "INVALID", 5));
        assertEquals("Invalid leave type: INVALID", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
}