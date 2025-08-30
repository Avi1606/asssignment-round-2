package com.lms.service;

import com.lms.dto.LeaveRequestRequest;
import com.lms.dto.LeaveRequestResponse;
import com.lms.exception.BusinessRuleException;
import com.lms.model.Department;
import com.lms.model.Employee;
import com.lms.model.LeaveRequest;
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
 * Unit tests for LeaveRequestService
 */
@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {
    
    @Mock
    private LeaveRequestRepository leaveRequestRepository;
    
    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private LeaveTransactionRepository leaveTransactionRepository;
    
    @InjectMocks
    private LeaveRequestService leaveRequestService;
    
    private Employee testEmployee;
    private LeaveRequestRequest testLeaveRequest;
    private LeaveRequest savedLeaveRequest;
    
    @BeforeEach
    void setUp() {
        Department department = new Department("Engineering", "Software Development");
        department.setId(1L);
        
        testEmployee = new Employee(
                "EMP001",
                "John",
                "Smith",
                "john.smith@company.com",
                LocalDate.of(2020, 1, 15),
                department
        );
        testEmployee.setId(1L);
        testEmployee.setAnnualLeaveBalance(20);
        testEmployee.setUsedAnnualLeave(5);
        
        testLeaveRequest = new LeaveRequestRequest();
        testLeaveRequest.setEmployeeId(1L);
        testLeaveRequest.setStartDate(LocalDate.now().plusDays(10));
        testLeaveRequest.setEndDate(LocalDate.now().plusDays(14));
        testLeaveRequest.setLeaveType(LeaveRequest.LeaveType.ANNUAL);
        testLeaveRequest.setReason("Family vacation");
        testLeaveRequest.setIsEmergency(false);
        
        savedLeaveRequest = new LeaveRequest(
                testEmployee,
                testLeaveRequest.getStartDate(),
                testLeaveRequest.getEndDate(),
                testLeaveRequest.getLeaveType(),
                testLeaveRequest.getReason()
        );
        savedLeaveRequest.setId(1L);
    }
    
    @Test
    void applyForLeave_Success() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaveRequests(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(savedLeaveRequest);
        
        // Act
        LeaveRequestResponse result = leaveRequestService.applyForLeave(testLeaveRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Smith", result.getEmployeeName());
        assertEquals(LeaveRequest.LeaveType.ANNUAL, result.getLeaveType());
        assertEquals(LeaveRequest.LeaveStatus.PENDING, result.getStatus());
        
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
        verify(leaveTransactionRepository).save(any());
    }
    
    @Test
    void applyForLeave_InvalidDateRange_ThrowsException() {
        // Arrange
        testLeaveRequest.setStartDate(LocalDate.now().plusDays(10));
        testLeaveRequest.setEndDate(LocalDate.now().plusDays(5)); // End before start
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        
        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> leaveRequestService.applyForLeave(testLeaveRequest)
        );
        
        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(leaveRequestRepository, never()).save(any());
    }
    
    @Test
    void applyForLeave_PastDate_ThrowsException() {
        // Arrange
        testLeaveRequest.setStartDate(LocalDate.now().minusDays(1));
        testLeaveRequest.setEndDate(LocalDate.now().plusDays(2));
        testLeaveRequest.setIsEmergency(false);
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        
        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> leaveRequestService.applyForLeave(testLeaveRequest)
        );
        
        assertEquals("Cannot apply for past dates unless it's an emergency", exception.getMessage());
        verify(leaveRequestRepository, never()).save(any());
    }
    
    @Test
    void applyForLeave_InsufficientBalance_ThrowsException() {
        // Arrange
        testEmployee.setUsedAnnualLeave(18); // Only 2 days left available
        testLeaveRequest.setStartDate(LocalDate.now().plusDays(10));
        testLeaveRequest.setEndDate(LocalDate.now().plusDays(14)); // Requesting 5 days
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaveRequests(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> leaveRequestService.applyForLeave(testLeaveRequest)
        );
        
        assertTrue(exception.getMessage().contains("Insufficient annual leave balance"));
        verify(leaveRequestRepository, never()).save(any());
    }
    
    @Test
    void applyForLeave_OverlappingRequests_ThrowsException() {
        // Arrange
        LeaveRequest overlappingRequest = new LeaveRequest();
        overlappingRequest.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaveRequests(any(), any(), any()))
                .thenReturn(Collections.singletonList(overlappingRequest));
        
        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> leaveRequestService.applyForLeave(testLeaveRequest)
        );
        
        assertEquals("You have overlapping leave requests for the selected dates", exception.getMessage());
        verify(leaveRequestRepository, never()).save(any());
    }
    
    @Test
    void applyForLeave_EmergencyLeave_AllowsPastDates() {
        // Arrange
        testLeaveRequest.setStartDate(LocalDate.now().minusDays(1));
        testLeaveRequest.setEndDate(LocalDate.now().plusDays(1));
        testLeaveRequest.setIsEmergency(true);
        testLeaveRequest.setLeaveType(LeaveRequest.LeaveType.EMERGENCY);
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveRequestRepository.findOverlappingLeaveRequests(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(savedLeaveRequest);
        
        // Act
        LeaveRequestResponse result = leaveRequestService.applyForLeave(testLeaveRequest);
        
        // Assert
        assertNotNull(result);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }
}