package com.lms.repository;

import com.lms.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for Department entity
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    /**
     * Find department by name
     * @param name the department name
     * @return Optional containing the department if found
     */
    Optional<Department> findByName(String name);
    
    /**
     * Check if department exists by name
     * @param name the department name
     * @return true if department exists
     */
    boolean existsByName(String name);
}