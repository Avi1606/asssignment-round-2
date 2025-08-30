package com.lms.repository;

import com.lms.model.Employee;
import com.lms.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity
 * 
 * @author Avi Patel
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByManagerId(Long managerId);

    List<Employee> findByRole(EmployeeRole role);

    List<Employee> findByActiveTrue();

    @Query("SELECT e FROM Employee e WHERE e.department.name = :departmentName AND e.active = true")
    List<Employee> findActiveEmployeesByDepartment(@Param("departmentName") String departmentName);

    @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId AND e.active = true")
    List<Employee> findActiveSubordinates(@Param("managerId") Long managerId);

    @Query("SELECT e FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "e.active = true")
    List<Employee> searchActiveEmployees(@Param("searchTerm") String searchTerm);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);
}