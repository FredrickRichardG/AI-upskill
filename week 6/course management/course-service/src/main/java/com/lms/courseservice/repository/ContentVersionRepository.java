package com.lms.courseservice.repository;

import com.lms.courseservice.model.ContentVersion;
import com.lms.courseservice.model.VersionStatus;
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
public interface ContentVersionRepository extends JpaRepository<ContentVersion, Long> {
    
    // Basic CRUD operations
    List<ContentVersion> findByCourseId(Long courseId);
    List<ContentVersion> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    Optional<ContentVersion> findByCourseIdAndVersion(Long courseId, String version);
    
    // Find versions by status
    List<ContentVersion> findByStatus(VersionStatus status);
    List<ContentVersion> findByCourseIdAndStatus(Long courseId, VersionStatus status);
    
    // Find versions by creator
    List<ContentVersion> findByCreatedBy(String createdBy);
    List<ContentVersion> findByCourseIdAndCreatedBy(Long courseId, String createdBy);
    
    // Find versions by creation date range
    @Query("SELECT cv FROM ContentVersion cv WHERE cv.createdAt BETWEEN :startDate AND :endDate")
    List<ContentVersion> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    // Find latest version for a course
    @Query("SELECT cv FROM ContentVersion cv " +
           "WHERE cv.course.id = :courseId " +
           "ORDER BY cv.createdAt DESC")
    List<ContentVersion> findLatestVersionsByCourse(@Param("courseId") Long courseId);
    
    // Find latest version for all courses
    @Query("SELECT cv FROM ContentVersion cv " +
           "WHERE cv.id IN (SELECT MAX(cv2.id) FROM ContentVersion cv2 GROUP BY cv2.course.id)")
    List<ContentVersion> findLatestVersionsForAllCourses();
    
    // Find versions by status and course
    @Query("SELECT cv FROM ContentVersion cv " +
           "WHERE cv.course.id = :courseId AND cv.status = :status " +
           "ORDER BY cv.createdAt DESC")
    List<ContentVersion> findByCourseAndStatus(@Param("courseId") Long courseId,
                                            @Param("status") VersionStatus status);
    
    // Find published versions
    @Query("SELECT cv FROM ContentVersion cv " +
           "WHERE cv.status = 'PUBLISHED' " +
           "ORDER BY cv.createdAt DESC")
    List<ContentVersion> findPublishedVersions();
    
    // Find versions in review
    @Query("SELECT cv FROM ContentVersion cv " +
           "WHERE cv.status = 'IN_REVIEW' " +
           "ORDER BY cv.createdAt ASC")
    List<ContentVersion> findVersionsInReview();
    
    // Find versions by change log keyword
    @Query("SELECT cv FROM ContentVersion cv " +
           "WHERE LOWER(cv.changeLog) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ContentVersion> searchByChangeLog(@Param("keyword") String keyword);
    
    // Count versions by status
    long countByStatus(VersionStatus status);
    long countByCourseIdAndStatus(Long courseId, VersionStatus status);
    
    // Find versions with pagination
    Page<ContentVersion> findAll(Pageable pageable);
    
    // Find versions ordered by creation date
    List<ContentVersion> findAllByOrderByCreatedAtDesc();
    
    // Find versions for specific course with pagination
    Page<ContentVersion> findByCourseId(Long courseId, Pageable pageable);
    
    // Find versions by status with pagination
    Page<ContentVersion> findByStatus(VersionStatus status, Pageable pageable);
    
    // Find versions by creator with pagination
    Page<ContentVersion> findByCreatedBy(String createdBy, Pageable pageable);
} 