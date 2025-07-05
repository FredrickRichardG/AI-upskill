package com.lms.courseservice.repository;

import com.lms.courseservice.model.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    
    // Basic CRUD operations
    Optional<Instructor> findByEmail(String email);
    List<Instructor> findByFirstNameContainingIgnoreCase(String firstName);
    List<Instructor> findByLastNameContainingIgnoreCase(String lastName);
    
    // Search instructors by name
    @Query("SELECT i FROM Instructor i WHERE " +
           "LOWER(i.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Instructor> searchByKeyword(@Param("keyword") String keyword);
    
    // Find instructors with course count
    @Query("SELECT i, COUNT(ca) as courseCount FROM Instructor i " +
           "LEFT JOIN i.assignments ca " +
           "GROUP BY i " +
           "ORDER BY courseCount DESC")
    List<Object[]> findInstructorsWithCourseCount();
    
    // Find instructors with minimum course count
    @Query("SELECT i FROM Instructor i " +
           "WHERE (SELECT COUNT(ca) FROM i.assignments ca) >= :minCourses")
    List<Instructor> findByMinCourseCount(@Param("minCourses") long minCourses);
    
    // Find instructors by expertise
    List<Instructor> findByExpertiseContainingIgnoreCase(String expertise);
    
    // Find instructors by qualification
    List<Instructor> findByQualification(String qualification);
    
    // Find instructors by experience range
    List<Instructor> findByYearsOfExperienceBetween(Integer minExperience, Integer maxExperience);
    List<Instructor> findByYearsOfExperienceGreaterThanEqual(Integer minExperience);
    
    // Find instructors with published courses
    @Query("SELECT DISTINCT i FROM Instructor i " +
           "JOIN i.assignments ca " +
           "JOIN ca.course c " +
           "WHERE c.status = 'PUBLISHED'")
    List<Instructor> findInstructorsWithPublishedCourses();
    
    // Find instructors by creation date range
    @Query("SELECT i FROM Instructor i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    List<Instructor> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                          @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find top instructors (most courses)
    @Query("SELECT i FROM Instructor i " +
           "WHERE (SELECT COUNT(ca) FROM i.assignments ca) > 0 " +
           "ORDER BY (SELECT COUNT(ca) FROM i.assignments ca) DESC")
    List<Instructor> findTopInstructors();
    
    // Find instructors ordered by name
    @Query("SELECT i FROM Instructor i ORDER BY i.firstName ASC, i.lastName ASC")
    List<Instructor> findAllOrderByName();
    
    // Find instructors with pagination
    Page<Instructor> findAll(Pageable pageable);
    
    // Count instructors
    long count();
    
    // Find active instructors
    List<Instructor> findByIsActiveTrue();
    
    // Count active instructors
    long countByIsActiveTrue();
    
    // Find instructors by assignment role
    @Query("SELECT DISTINCT i FROM Instructor i " +
           "JOIN i.assignments ca " +
           "WHERE ca.role = :role")
    List<Instructor> findByAssignmentRole(@Param("role") String role);
} 