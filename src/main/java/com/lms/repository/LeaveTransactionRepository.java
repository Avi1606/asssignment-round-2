package com.lms.repository;

import com.lms.model.LeaveTransaction;
import com.lms.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for LeaveTransaction entity
 * 
 * @author Avi Patel
 */
@Repository
public interface LeaveTransactionRepository extends JpaRepository<LeaveTransaction, Long> {

    List<LeaveTransaction> findByLeaveRequestId(Long leaveRequestId);

    List<LeaveTransaction> findByPerformedById(Long performedById);

    List<LeaveTransaction> findByTransactionType(TransactionType transactionType);

    @Query("SELECT lt FROM LeaveTransaction lt WHERE " +
           "lt.leaveRequest.employee.id = :employeeId " +
           "ORDER BY lt.transactionDate DESC")
    List<LeaveTransaction> findByEmployeeIdOrderByDateDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT lt FROM LeaveTransaction lt WHERE " +
           "lt.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY lt.transactionDate DESC")
    List<LeaveTransaction> findByDateRangeOrderByDateDesc(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT lt FROM LeaveTransaction lt WHERE " +
           "lt.leaveRequest.employee.department.id = :departmentId " +
           "ORDER BY lt.transactionDate DESC")
    List<LeaveTransaction> findByDepartmentIdOrderByDateDesc(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(lt) FROM LeaveTransaction lt WHERE " +
           "lt.transactionType = :transactionType AND " +
           "lt.transactionDate >= :fromDate")
    long countByTransactionTypeAndDateAfter(@Param("transactionType") TransactionType transactionType,
                                          @Param("fromDate") LocalDateTime fromDate);
}