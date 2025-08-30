package com.lms.repository;

import com.lms.model.LeaveRequest;
import com.lms.model.LeaveStatus;
import com.lms.model.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LeaveRequest entity
 * 
 * @author Avi Patel
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.manager.id = :managerId AND lr.status = :status")
    List<LeaveRequest> findByManagerIdAndStatus(@Param("managerId") Long managerId, @Param("status") LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.department.id = :departmentId")
    List<LeaveRequest> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE " +
           "lr.employee.id = :employeeId AND " +
           "lr.status IN ('PENDING', 'APPROVED') AND " +
           "(lr.startDate <= :endDate AND lr.endDate >= :startDate)")
    List<LeaveRequest> findOverlappingLeaves(@Param("employeeId") Long employeeId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr WHERE " +
           "lr.startDate >= :startDate AND lr.endDate <= :endDate AND " +
           "lr.status = 'APPROVED'")
    List<LeaveRequest> findApprovedLeavesBetweenDates(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr WHERE " +
           "lr.employee.id = :employeeId AND " +
           "lr.leaveType = :leaveType AND " +
           "lr.status = 'APPROVED' AND " +
           "YEAR(lr.startDate) = :year")
    List<LeaveRequest> findApprovedLeavesByEmployeeTypeAndYear(@Param("employeeId") Long employeeId,
                                                             @Param("leaveType") LeaveType leaveType,
                                                             @Param("year") int year);

    @Query("SELECT COUNT(lr) FROM LeaveRequest lr WHERE " +
           "lr.employee.department.id = :departmentId AND " +
           "lr.status = 'PENDING'")
    long countPendingLeavesByDepartment(@Param("departmentId") Long departmentId);

    @Query("SELECT lr FROM LeaveRequest lr LEFT JOIN FETCH lr.employee e LEFT JOIN FETCH e.department WHERE lr.id = :id")
    Optional<LeaveRequest> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT SUM(lr.totalDays) FROM LeaveRequest lr WHERE " +
           "lr.employee.id = :employeeId AND " +
           "lr.leaveType = :leaveType AND " +
           "lr.status = 'APPROVED' AND " +
           "YEAR(lr.startDate) = :year")
    Integer sumApprovedDaysByEmployeeTypeAndYear(@Param("employeeId") Long employeeId,
                                               @Param("leaveType") LeaveType leaveType,
                                               @Param("year") int year);
}