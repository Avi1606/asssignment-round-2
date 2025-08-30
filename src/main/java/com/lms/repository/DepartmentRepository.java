package com.lms.repository;

import com.lms.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Department entity
 * 
 * @author Avi Patel
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    List<Department> findByNameContainingIgnoreCase(String name);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees e WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(@Param("id") Long id);

    @Query("SELECT d FROM Department d WHERE SIZE(d.employees) > 0")
    List<Department> findDepartmentsWithEmployees();

    boolean existsByName(String name);
}