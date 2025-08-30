package com.lms.repository;

import com.lms.model.LeaveTransaction;
import com.lms.model.LeaveTransaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for LeaveTransaction entity operations
 */
@Repository
public interface LeaveTransactionRepository extends JpaRepository<LeaveTransaction, Long> {

    /**
     * Find transactions by leave request
     */
    List<LeaveTransaction> findByLeaveRequestIdOrderByCreatedAtDesc(Long leaveRequestId);

    /**
     * Find transactions by employee
     */
    @Query("SELECT lt FROM LeaveTransaction lt " +
           "WHERE lt.leaveRequest.employee.id = :employeeId " +
           "ORDER BY lt.createdAt DESC")
    List<LeaveTransaction> findByEmployeeIdOrderByCreatedAtDesc(@Param("employeeId") Long employeeId);

    /**
     * Find transactions by transaction type
     */
    List<LeaveTransaction> findByTransactionTypeOrderByCreatedAtDesc(TransactionType transactionType);

    /**
     * Find transactions by date range
     */
    List<LeaveTransaction> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find transactions performed by specific user
     */
    List<LeaveTransaction> findByPerformedByIdOrderByCreatedAtDesc(Long performedById);

    /**
     * Find all transactions with leave request and employee details
     */
    @Query("SELECT lt FROM LeaveTransaction lt " +
           "LEFT JOIN FETCH lt.leaveRequest lr " +
           "LEFT JOIN FETCH lr.employee " +
           "LEFT JOIN FETCH lt.performedBy " +
           "ORDER BY lt.createdAt DESC")
    List<LeaveTransaction> findAllWithDetails();
}