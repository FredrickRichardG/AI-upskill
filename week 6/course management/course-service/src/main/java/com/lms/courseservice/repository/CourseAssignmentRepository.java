package com.lms.courseservice.repository;

import com.lms.courseservice.model.CourseAssignment;
import com.lms.courseservice.model.AssignmentRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
    
    // Basic CRUD operations
    List<CourseAssignment> findByCourseId(Long courseId);
    List<CourseAssignment> findByInstructorId(Long instructorId);
    Optional<CourseAssignment> findByCourseIdAndInstructorId(Long courseId, Long instructorId);
    
    // Find assignments by role
    List<CourseAssignment> findByRole(AssignmentRole role);
    List<CourseAssignment> findByCourseIdAndRole(Long courseId, AssignmentRole role);
    List<CourseAssignment> findByInstructorIdAndRole(Long instructorId, AssignmentRole role);
    
    // Find assignments by assignment date range
    List<CourseAssignment> findByAssignedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find assignments by creation date range
    @Query("SELECT ca FROM CourseAssignment ca WHERE ca.createdAt BETWEEN :startDate AND :endDate")
    List<CourseAssignment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
    
    // Find assignments for published courses
    @Query("SELECT ca FROM CourseAssignment ca " +
           "JOIN ca.course c " +
           "WHERE c.status = 'PUBLISHED'")
    List<CourseAssignment> findAssignmentsForPublishedCourses();
    
    // Find assignments for specific course status
    @Query("SELECT ca FROM CourseAssignment ca " +
           "JOIN ca.course c " +
           "WHERE c.status = :status")
    List<CourseAssignment> findByCourseStatus(@Param("status") String status);
    
    // Find assignments with pagination
    Page<CourseAssignment> findAll(Pageable pageable);
    
    // Count assignments by role
    long countByRole(AssignmentRole role);
    
    // Count assignments by course
    long countByCourseId(Long courseId);
    
    // Count assignments by instructor
    long countByInstructorId(Long instructorId);
    
    // Count assignments by instructor and role
    long countByInstructorIdAndRole(Long instructorId, AssignmentRole role);
    
    // Find assignments ordered by assignment date
    List<CourseAssignment> findAllByOrderByAssignedAtDesc();
    
    // Find assignments for specific instructor and course
    @Query("SELECT ca FROM CourseAssignment ca " +
           "WHERE ca.instructor.id = :instructorId AND ca.course.id = :courseId")
    Optional<CourseAssignment> findByInstructorAndCourse(@Param("instructorId") Long instructorId,
                                                       @Param("courseId") Long courseId);
    
    // Find assignments with specific role for a course
    @Query("SELECT ca FROM CourseAssignment ca " +
           "WHERE ca.course.id = :courseId AND ca.role = :role")
    List<CourseAssignment> findByCourseAndRole(@Param("courseId") Long courseId,
                                             @Param("role") AssignmentRole role);
    
    // Find assignments with specific role for an instructor
    @Query("SELECT ca FROM CourseAssignment ca " +
           "WHERE ca.instructor.id = :instructorId AND ca.role = :role")
    List<CourseAssignment> findByInstructorAndRole(@Param("instructorId") Long instructorId,
                                                 @Param("role") AssignmentRole role);
    
    // Delete assignments by course
    void deleteByCourseId(Long courseId);
} 