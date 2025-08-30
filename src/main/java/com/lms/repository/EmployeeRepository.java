package com.lms.repository;

import com.lms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Find employee by employee ID
     * @param employeeId the employee ID
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmployeeId(String employeeId);
    
    /**
     * Find employee by email
     * @param email the employee email
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmail(String email);
    
    /**
     * Find employees by department ID
     * @param departmentId the department ID
     * @return List of employees in the department
     */
    List<Employee> findByDepartmentId(Long departmentId);
    
    /**
     * Find employees by manager ID
     * @param managerId the manager ID
     * @return List of employees under the manager
     */
    List<Employee> findByManagerId(Long managerId);
    
    /**
     * Find employees by status
     * @param status the employee status
     * @return List of employees with the given status
     */
    List<Employee> findByStatus(Employee.EmployeeStatus status);
    
    /**
     * Check if employee exists by employee ID
     * @param employeeId the employee ID
     * @return true if employee exists
     */
    boolean existsByEmployeeId(String employeeId);
    
    /**
     * Check if employee exists by email
     * @param email the employee email
     * @return true if employee exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find employees with low leave balance
     * @param threshold the balance threshold
     * @return List of employees with leave balance below threshold
     */
    @Query("SELECT e FROM Employee e WHERE (e.annualLeaveBalance - e.usedAnnualLeave) <= :threshold")
    List<Employee> findEmployeesWithLowLeaveBalance(@Param("threshold") Integer threshold);
    
    /**
     * Count employees by department
     * @param departmentId the department ID
     * @return count of employees in the department
     */
    long countByDepartmentId(Long departmentId);
}