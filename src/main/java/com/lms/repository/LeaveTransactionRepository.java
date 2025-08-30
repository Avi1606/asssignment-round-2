package com.lms.repository;

import com.lms.model.LeaveTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for LeaveTransaction entity
 */
@Repository
public interface LeaveTransactionRepository extends JpaRepository<LeaveTransaction, Long> {
    
    /**
     * Find transactions by leave request ID
     * @param leaveRequestId the leave request ID
     * @return List of transactions for the leave request
     */
    List<LeaveTransaction> findByLeaveRequestIdOrderByCreatedAtDesc(Long leaveRequestId);
    
    /**
     * Find transactions by employee ID
     * @param employeeId the employee ID
     * @return List of transactions for the employee
     */
    List<LeaveTransaction> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    /**
     * Find transactions by transaction type
     * @param transactionType the transaction type
     * @return List of transactions of the given type
     */
    List<LeaveTransaction> findByTransactionTypeOrderByCreatedAtDesc(
            LeaveTransaction.TransactionType transactionType);
    
    /**
     * Find transactions performed by a specific user
     * @param performedById the ID of the user who performed the transaction
     * @return List of transactions performed by the user
     */
    List<LeaveTransaction> findByPerformedByIdOrderByCreatedAtDesc(Long performedById);
    
    /**
     * Find transactions by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List of transactions within the date range
     */
    @Query("SELECT lt FROM LeaveTransaction lt WHERE lt.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY lt.createdAt DESC")
    List<LeaveTransaction> findTransactionsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get audit trail for a specific leave request
     * @param leaveRequestId the leave request ID
     * @return List of all transactions for the leave request in chronological order
     */
    @Query("SELECT lt FROM LeaveTransaction lt WHERE lt.leaveRequest.id = :leaveRequestId " +
           "ORDER BY lt.createdAt ASC")
    List<LeaveTransaction> getAuditTrailForLeaveRequest(@Param("leaveRequestId") Long leaveRequestId);
}