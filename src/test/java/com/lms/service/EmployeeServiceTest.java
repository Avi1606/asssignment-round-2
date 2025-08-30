package com.lms.service;

import com.lms.dto.EmployeeRequest;
import com.lms.dto.EmployeeResponse;
import com.lms.exception.BusinessRuleException;
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
    private EmployeeRequest testEmployeeRequest;
    
    @BeforeEach
    void setUp() {
        testDepartment = new Department("Engineering", "Software Development");
        testDepartment.setId(1L);
        
        testEmployee = new Employee(
                "EMP001",
                "John",
                "Smith",
                "john.smith@company.com",
                LocalDate.of(2020, 1, 15),
                testDepartment
        );
        testEmployee.setId(1L);
        
        testEmployeeRequest = new EmployeeRequest();
        testEmployeeRequest.setEmployeeId("EMP001");
        testEmployeeRequest.setFirstName("John");
        testEmployeeRequest.setLastName("Smith");
        testEmployeeRequest.setEmail("john.smith@company.com");
        testEmployeeRequest.setJoiningDate(LocalDate.of(2020, 1, 15));
        testEmployeeRequest.setDepartmentId(1L);
    }
    
    @Test
    void createEmployee_Success() {
        // Arrange
        when(employeeRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.smith@company.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponse result = employeeService.createEmployee(testEmployeeRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("John Smith", result.getFullName());
        assertEquals("john.smith@company.com", result.getEmail());
        assertEquals("Engineering", result.getDepartmentName());
        
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void createEmployee_DuplicateEmployeeId_ThrowsException() {
        // Arrange
        when(employeeRepository.existsByEmployeeId("EMP001")).thenReturn(true);
        
        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> employeeService.createEmployee(testEmployeeRequest)
        );
        
        assertEquals("Employee ID already exists: EMP001", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }
    
    @Test
    void createEmployee_DuplicateEmail_ThrowsException() {
        // Arrange
        when(employeeRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.smith@company.com")).thenReturn(true);
        
        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> employeeService.createEmployee(testEmployeeRequest)
        );
        
        assertEquals("Email already exists: john.smith@company.com", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }
    
    @Test
    void createEmployee_DepartmentNotFound_ThrowsException() {
        // Arrange
        when(employeeRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.smith@company.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.createEmployee(testEmployeeRequest)
        );
        
        assertEquals("Department not found with id: '1'", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }
    
    @Test
    void getEmployeeById_Success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        
        // Act
        EmployeeResponse result = employeeService.getEmployeeById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("John Smith", result.getFullName());
    }
    
    @Test
    void getEmployeeById_NotFound_ThrowsException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(1L)
        );
        
        assertEquals("Employee not found with id: '1'", exception.getMessage());
    }
    
    @Test
    void updateEmployee_Success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponse result = employeeService.updateEmployee(1L, testEmployeeRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("John Smith", result.getFullName());
        verify(employeeRepository).save(any(Employee.class));
    }
}