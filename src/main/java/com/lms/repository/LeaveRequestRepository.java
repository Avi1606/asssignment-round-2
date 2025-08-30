package com.lms.repository;

import com.lms.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for LeaveRequest entity
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    /**
     * Find leave requests by employee ID
     * @param employeeId the employee ID
     * @return List of leave requests for the employee
     */
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    
    /**
     * Find leave requests by status
     * @param status the leave request status
     * @return List of leave requests with the given status
     */
    List<LeaveRequest> findByStatus(LeaveRequest.LeaveStatus status);
    
    /**
     * Find leave requests by approver ID
     * @param approverId the approver ID
     * @return List of leave requests assigned to the approver
     */
    List<LeaveRequest> findByApproverId(Long approverId);
    
    /**
     * Find leave requests by employee ID and status
     * @param employeeId the employee ID
     * @param status the leave request status
     * @return List of leave requests for the employee with given status
     */
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveRequest.LeaveStatus status);
    
    /**
     * Find leave requests by leave type
     * @param leaveType the leave type
     * @return List of leave requests of the given type
     */
    List<LeaveRequest> findByLeaveType(LeaveRequest.LeaveType leaveType);
    
    /**
     * Find overlapping leave requests for an employee
     * @param employeeId the employee ID
     * @param startDate the start date of the new leave request
     * @param endDate the end date of the new leave request
     * @return List of overlapping leave requests
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.status IN ('PENDING', 'APPROVED') " +
           "AND ((lr.startDate BETWEEN :startDate AND :endDate) " +
           "OR (lr.endDate BETWEEN :startDate AND :endDate) " +
           "OR (lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    List<LeaveRequest> findOverlappingLeaveRequests(@Param("employeeId") Long employeeId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Find pending leave requests for manager approval
     * @param managerId the manager ID
     * @return List of pending leave requests for the manager's team
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.manager.id = :managerId " +
           "AND lr.status = 'PENDING'")
    List<LeaveRequest> findPendingLeaveRequestsForManager(@Param("managerId") Long managerId);
    
    /**
     * Find leave requests by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List of leave requests within the date range
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.startDate >= :startDate AND lr.endDate <= :endDate")
    List<LeaveRequest> findLeaveRequestsByDateRange(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Count approved leave days for an employee by leave type and year
     * @param employeeId the employee ID
     * @param leaveType the leave type
     * @param year the year
     * @return total approved leave days
     */
    @Query("SELECT COALESCE(SUM(lr.daysRequested), 0) FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.leaveType = :leaveType " +
           "AND lr.status = 'APPROVED' " +
           "AND YEAR(lr.startDate) = :year")
    Integer countApprovedLeaveDaysByTypeAndYear(@Param("employeeId") Long employeeId,
                                              @Param("leaveType") LeaveRequest.LeaveType leaveType,
                                              @Param("year") Integer year);
}