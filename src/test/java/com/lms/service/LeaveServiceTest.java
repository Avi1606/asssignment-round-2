package com.lms.service;

import com.lms.dto.LeaveRequestCreateDTO;
import com.lms.dto.LeaveRequestResponseDTO;
import com.lms.exception.BusinessException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.model.Department;
import com.lms.model.Employee;
import com.lms.model.LeaveRequest;
import com.lms.model.LeaveTransaction;
import com.lms.repository.EmployeeRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.LeaveTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LeaveService
 */
@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTransactionRepository leaveTransactionRepository;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private LeaveService leaveService;

    private Employee testEmployee;
    private Employee testManager;
    private Department testDepartment;
    private LeaveRequestCreateDTO createDTO;
    private LeaveRequest testLeaveRequest;

    @BeforeEach
    void setUp() {
        testDepartment = new Department("Engineering", "Software Development");
        testDepartment.setId(1L);

        testManager = new Employee("Manager", "manager@company.com", 
                                 LocalDate.of(2022, 1, 15), testDepartment);
        testManager.setId(1L);

        testEmployee = new Employee("Test Employee", "test@company.com", 
                                  LocalDate.of(2023, 1, 15), testDepartment);
        testEmployee.setId(2L);
        testEmployee.setManager(testManager);

        createDTO = new LeaveRequestCreateDTO(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                LeaveRequest.LeaveType.ANNUAL,
                "Test vacation",
                2L
        );

        testLeaveRequest = new LeaveRequest(
                createDTO.getStartDate(),
                createDTO.getEndDate(),
                createDTO.getLeaveType(),
                createDTO.getReason(),
                testEmployee
        );
        testLeaveRequest.setId(1L);
    }

    @Test
    void applyForLeave_Success() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaves(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);

        // Act
        LeaveRequestResponseDTO result = leaveService.applyForLeave(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals(LeaveRequest.LeaveStatus.PENDING, result.getStatus());
        assertEquals("Test vacation", result.getReason());
        assertEquals(3, result.getDaysRequested());
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
        verify(leaveTransactionRepository).save(any(LeaveTransaction.class));
    }

    @Test
    void applyForLeave_EmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> leaveService.applyForLeave(createDTO));
        assertEquals("Employee not found with ID: 2", exception.getMessage());
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    void applyForLeave_InactiveEmployee() {
        // Arrange
        testEmployee.setIsActive(false);
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> leaveService.applyForLeave(createDTO));
        assertEquals("Cannot apply leave for inactive employee", exception.getMessage());
    }

    @Test
    void applyForLeave_InsufficientBalance() {
        // Arrange
        testEmployee.setAnnualLeaveBalance(1); // Less than required 3 days
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaves(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> leaveService.applyForLeave(createDTO));
        assertTrue(exception.getMessage().contains("Insufficient Annual Leave balance"));
    }

    @Test
    void applyForLeave_OverlappingLeaves() {
        // Arrange
        LeaveRequest overlappingLeave = new LeaveRequest(
                LocalDate.now().plusDays(11),
                LocalDate.now().plusDays(13),
                LeaveRequest.LeaveType.SICK,
                "Overlapping leave",
                testEmployee
        );

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaves(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(overlappingLeave));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> leaveService.applyForLeave(createDTO));
        assertEquals("Leave request overlaps with existing leave(s)", exception.getMessage());
    }

    @Test
    void approveLeave_Success() {
        // Arrange
        testLeaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testLeaveRequest));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);

        // Act
        LeaveRequestResponseDTO result = leaveService.approveLeave(1L, 1L, "Approved");

        // Assert
        assertNotNull(result);
        assertEquals(LeaveRequest.LeaveStatus.APPROVED, testLeaveRequest.getStatus());
        assertEquals("Approved", testLeaveRequest.getApproverComments());
        verify(employeeService).updateLeaveBalance(eq(2L), eq("ANNUAL"), eq(-3));
        verify(leaveTransactionRepository, times(2)).save(any(LeaveTransaction.class));
    }

    @Test
    void approveLeave_LeaveRequestNotFound() {
        // Arrange
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> leaveService.approveLeave(1L, 1L, "Approved"));
        assertEquals("Leave request not found with ID: 1", exception.getMessage());
    }

    @Test
    void approveLeave_NotPendingStatus() {
        // Arrange
        testLeaveRequest.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testLeaveRequest));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> leaveService.approveLeave(1L, 1L, "Approved"));
        assertEquals("Leave request is not in pending status", exception.getMessage());
    }

    @Test
    void approveLeave_EmployeeCannotApproveSelf() {
        // Arrange
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testLeaveRequest));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> leaveService.approveLeave(1L, 2L, "Approved"));
        assertEquals("Employee cannot approve their own leave request", exception.getMessage());
    }

    @Test
    void rejectLeave_Success() {
        // Arrange
        testLeaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testLeaveRequest));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(testLeaveRequest);

        // Act
        LeaveRequestResponseDTO result = leaveService.rejectLeave(1L, 1L, "Rejected");

        // Assert
        assertNotNull(result);
        assertEquals(LeaveRequest.LeaveStatus.REJECTED, testLeaveRequest.getStatus());
        assertEquals("Rejected", testLeaveRequest.getApproverComments());
        verify(leaveTransactionRepository).save(any(LeaveTransaction.class));
        verify(employeeService, never()).updateLeaveBalance(anyLong(), anyString(), anyInt());
    }

    @Test
    void getLeaveRequestById_Success() {
        // Arrange
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testLeaveRequest));

        // Act
        LeaveRequestResponseDTO result = leaveService.getLeaveRequestById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test vacation", result.getReason());
    }

    @Test
    void getLeaveRequestById_NotFound() {
        // Arrange
        when(leaveRequestRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> leaveService.getLeaveRequestById(1L));
        assertEquals("Leave request not found with ID: 1", exception.getMessage());
    }
}