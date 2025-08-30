package com.lms.repository;

import com.lms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity operations
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find employee by email
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Find all active employees
     */
    List<Employee> findByIsActiveTrue();

    /**
     * Find employees by department
     */
    List<Employee> findByDepartmentIdAndIsActiveTrue(Long departmentId);

    /**
     * Find employees by manager
     */
    List<Employee> findByManagerIdAndIsActiveTrue(Long managerId);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find managers (employees who have subordinates)
     */
    @Query("SELECT DISTINCT e FROM Employee e WHERE e.id IN " +
           "(SELECT DISTINCT s.manager.id FROM Employee s WHERE s.manager IS NOT NULL) " +
           "AND e.isActive = true")
    List<Employee> findAllManagers();

    /**
     * Find employee with department and manager details
     */
    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.department " +
           "LEFT JOIN FETCH e.manager " +
           "WHERE e.id = :id AND e.isActive = true")
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all employees with department and manager details
     */
    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.department " +
           "LEFT JOIN FETCH e.manager " +
           "WHERE e.isActive = true " +
           "ORDER BY e.name")
    List<Employee> findAllWithDetails();
}