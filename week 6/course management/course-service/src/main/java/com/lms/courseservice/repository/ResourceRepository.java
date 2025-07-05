package com.lms.courseservice.repository;

import com.lms.courseservice.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    // Basic CRUD operations
    List<Resource> findByCourseId(Long courseId);
    List<Resource> findByModuleId(Long moduleId);
    List<Resource> findByLessonId(Long lessonId);
    
    // Find resources by file type
    List<Resource> findByFileType(String fileType);
    List<Resource> findByCourseIdAndFileType(Long courseId, String fileType);
    List<Resource> findByModuleIdAndFileType(Long moduleId, String fileType);
    List<Resource> findByLessonIdAndFileType(Long lessonId, String fileType);
    
    // Find resources by MIME type
    List<Resource> findByMimeType(String mimeType);
    List<Resource> findByCourseIdAndMimeType(Long courseId, String mimeType);
    
    // Find resources by file size range
    List<Resource> findByFileSizeBetween(Long minSize, Long maxSize);
    List<Resource> findByCourseIdAndFileSizeBetween(Long courseId, Long minSize, Long maxSize);
    
    // Find public resources
    List<Resource> findByIsPublicTrue();
    List<Resource> findByCourseIdAndIsPublicTrue(Long courseId);
    
    // Find private resources
    List<Resource> findByIsPublicFalse();
    List<Resource> findByCourseIdAndIsPublicFalse(Long courseId);
    
    // Search resources by name and description
    @Query("SELECT r FROM Resource r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Resource> searchByKeyword(@Param("keyword") String keyword);
    
    // Search resources by course
    @Query("SELECT r FROM Resource r " +
           "WHERE r.course.id = :courseId AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Resource> searchByCourseAndKeyword(@Param("courseId") Long courseId,
                                          @Param("keyword") String keyword);
    
    // Find resources by creation date range
    @Query("SELECT r FROM Resource r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<Resource> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                        @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find resources by course and creation date range
    @Query("SELECT r FROM Resource r " +
           "WHERE r.course.id = :courseId AND r.createdAt BETWEEN :startDate AND :endDate")
    List<Resource> findByCourseAndCreatedAtBetween(@Param("courseId") Long courseId,
                                                 @Param("startDate") java.time.LocalDateTime startDate,
                                                 @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find largest resources
    @Query("SELECT r FROM Resource r ORDER BY r.fileSize DESC")
    List<Resource> findLargestResources();
    
    // Find largest resources by course
    @Query("SELECT r FROM Resource r " +
           "WHERE r.course.id = :courseId " +
           "ORDER BY r.fileSize DESC")
    List<Resource> findLargestResourcesByCourse(@Param("courseId") Long courseId);
    
    // Count resources by file type
    long countByFileType(String fileType);
    long countByCourseIdAndFileType(Long courseId, String fileType);
    
    // Count resources by course
    long countByCourseId(Long courseId);
    long countByModuleId(Long moduleId);
    long countByLessonId(Long lessonId);
    
    // Find resources with pagination
    Page<Resource> findAll(Pageable pageable);
    
    // Find resources by course with pagination
    Page<Resource> findByCourseId(Long courseId, Pageable pageable);
    
    // Find resources by file type with pagination
    Page<Resource> findByFileType(String fileType, Pageable pageable);
    
    // Find resources ordered by creation date
    List<Resource> findAllByOrderByCreatedAtDesc();
    
    // Find resources by course ordered by creation date
    List<Resource> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    
    // Find resources by file size greater than
    List<Resource> findByFileSizeGreaterThan(Long minSize);
    
    // Find resources by file size less than
    List<Resource> findByFileSizeLessThan(Long maxSize);
} 