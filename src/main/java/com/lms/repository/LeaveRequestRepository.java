package com.lms.repository;

import com.lms.model.LeaveRequest;
import com.lms.model.LeaveRequest.LeaveStatus;
import com.lms.model.LeaveRequest.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LeaveRequest entity operations
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    /**
     * Find leave requests by employee
     */
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    /**
     * Find leave requests by status
     */
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(LeaveStatus status);

    /**
     * Find leave requests by employee and status
     */
    List<LeaveRequest> findByEmployeeIdAndStatusOrderByCreatedAtDesc(Long employeeId, LeaveStatus status);

    /**
     * Find overlapping leave requests for an employee
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.status IN ('PENDING', 'APPROVED') " +
           "AND ((lr.startDate <= :endDate AND lr.endDate >= :startDate))")
    List<LeaveRequest> findOverlappingLeaves(@Param("employeeId") Long employeeId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * Find leave requests that need approval by manager
     */
    @Query("SELECT lr FROM LeaveRequest lr " +
           "WHERE lr.employee.manager.id = :managerId " +
           "AND lr.status = 'PENDING' " +
           "ORDER BY lr.createdAt ASC")
    List<LeaveRequest> findPendingLeavesByManager(@Param("managerId") Long managerId);

    /**
     * Find leave requests with employee and approver details
     */
    @Query("SELECT lr FROM LeaveRequest lr " +
           "LEFT JOIN FETCH lr.employee " +
           "LEFT JOIN FETCH lr.approvedBy " +
           "WHERE lr.id = :id")
    Optional<LeaveRequest> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all leave requests with employee details
     */
    @Query("SELECT lr FROM LeaveRequest lr " +
           "LEFT JOIN FETCH lr.employee e " +
           "LEFT JOIN FETCH e.department " +
           "LEFT JOIN FETCH lr.approvedBy " +
           "ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findAllWithDetails();

    /**
     * Find approved leave requests by date range
     */
    @Query("SELECT lr FROM LeaveRequest lr " +
           "WHERE lr.status = 'APPROVED' " +
           "AND lr.startDate <= :endDate " +
           "AND lr.endDate >= :startDate")
    List<LeaveRequest> findApprovedLeavesByDateRange(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * Count leave requests by employee and leave type for current year
     */
    @Query("SELECT COUNT(lr) FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.leaveType = :leaveType " +
           "AND lr.status = 'APPROVED' " +
           "AND YEAR(lr.startDate) = YEAR(CURRENT_DATE)")
    Long countApprovedLeavesByTypeForCurrentYear(@Param("employeeId") Long employeeId,
                                               @Param("leaveType") LeaveType leaveType);
}