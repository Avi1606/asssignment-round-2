package com.lms.repository;

import com.lms.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Department entity operations
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Find department by name (case-insensitive)
     */
    Optional<Department> findByNameIgnoreCase(String name);

    /**
     * Find all active departments
     */
    List<Department> findByIsActiveTrue();

    /**
     * Check if department name exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find departments with employee count
     */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.isActive = true")
    List<Department> findAllActiveDepartmentsWithEmployees();
}