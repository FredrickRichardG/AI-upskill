package com.lms.courseservice.repository;

import com.lms.courseservice.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    // Basic CRUD operations
    List<Lesson> findByModuleId(Long moduleId);
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
    Optional<Lesson> findByModuleIdAndOrderIndex(Long moduleId, Integer orderIndex);
    
    // Search lessons by title and description
    @Query("SELECT l FROM Lesson l WHERE " +
           "LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Lesson> searchByKeyword(@Param("keyword") String keyword);
    
    // Search lessons by module and keyword
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.module.id = :moduleId AND " +
           "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Lesson> searchByModuleAndKeyword(@Param("moduleId") Long moduleId,
                                         @Param("keyword") String keyword);
    
    // Find lessons by course
    @Query("SELECT l FROM Lesson l " +
           "JOIN l.module m " +
           "WHERE m.course.id = :courseId")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);
    
    // Find lessons by course ordered by module and lesson order
    @Query("SELECT l FROM Lesson l " +
           "JOIN l.module m " +
           "WHERE m.course.id = :courseId " +
           "ORDER BY m.orderIndex ASC, l.orderIndex ASC")
    List<Lesson> findByCourseIdOrdered(@Param("courseId") Long courseId);
    
    // Find lessons by duration range
    List<Lesson> findByDurationBetween(Integer minDuration, Integer maxDuration);
    List<Lesson> findByModuleIdAndDurationBetween(Long moduleId, Integer minDuration, Integer maxDuration);
    
    // Find published lessons
    List<Lesson> findByIsPublishedTrue();
    List<Lesson> findByModuleIdAndIsPublishedTrue(Long moduleId);
    
    // Find unpublished lessons
    List<Lesson> findByIsPublishedFalse();
    List<Lesson> findByModuleIdAndIsPublishedFalse(Long moduleId);
    
    // Find lessons with video content
    @Query("SELECT l FROM Lesson l WHERE l.videoUrl IS NOT NULL AND l.videoUrl != ''")
    List<Lesson> findLessonsWithVideo();
    
    // Find lessons by course with video content
    @Query("SELECT l FROM Lesson l " +
           "JOIN l.module m " +
           "WHERE m.course.id = :courseId AND l.videoUrl IS NOT NULL AND l.videoUrl != ''")
    List<Lesson> findLessonsWithVideoByCourse(@Param("courseId") Long courseId);
    
    // Find lessons by creation date range
    @Query("SELECT l FROM Lesson l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<Lesson> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find lessons by module and creation date range
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.module.id = :moduleId AND l.createdAt BETWEEN :startDate AND :endDate")
    List<Lesson> findByModuleAndCreatedAtBetween(@Param("moduleId") Long moduleId,
                                               @Param("startDate") java.time.LocalDateTime startDate,
                                               @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find lessons with resources
    @Query("SELECT l FROM Lesson l " +
           "WHERE (SELECT COUNT(r) FROM l.resources r) > 0")
    List<Lesson> findLessonsWithResources();
    
    // Find lessons by resource count
    @Query("SELECT l FROM Lesson l " +
           "WHERE (SELECT COUNT(r) FROM l.resources r) >= :minResources")
    List<Lesson> findByMinResources(@Param("minResources") long minResources);
    
    // Find lessons by module with resources
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.module.id = :moduleId AND (SELECT COUNT(r) FROM l.resources r) > 0")
    List<Lesson> findLessonsWithResourcesByModule(@Param("moduleId") Long moduleId);
    
    // Count lessons by module
    long countByModuleId(Long moduleId);
    
    // Count published lessons by module
    long countByModuleIdAndIsPublishedTrue(Long moduleId);
    
    // Find lessons with pagination
    Page<Lesson> findAll(Pageable pageable);
    
    // Find lessons by module with pagination
    Page<Lesson> findByModuleId(Long moduleId, Pageable pageable);
    
    // Find lessons ordered by creation date
    List<Lesson> findAllByOrderByCreatedAtDesc();
    
    // Find lessons by module ordered by creation date
    List<Lesson> findByModuleIdOrderByCreatedAtDesc(Long moduleId);
    
    // Find lessons by order index range
    List<Lesson> findByOrderIndexBetween(Integer minOrder, Integer maxOrder);
    List<Lesson> findByModuleIdAndOrderIndexBetween(Long moduleId, Integer minOrder, Integer maxOrder);
    
    // Find next lesson in sequence
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.module.id = :moduleId AND l.orderIndex > :currentOrder " +
           "ORDER BY l.orderIndex ASC")
    List<Lesson> findNextLessons(@Param("moduleId") Long moduleId, @Param("currentOrder") Integer currentOrder);
    
    // Find previous lesson in sequence
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.module.id = :moduleId AND l.orderIndex < :currentOrder " +
           "ORDER BY l.orderIndex DESC")
    List<Lesson> findPreviousLessons(@Param("moduleId") Long moduleId, @Param("currentOrder") Integer currentOrder);
    
    // Find lessons with longest duration
    @Query("SELECT l FROM Lesson l ORDER BY l.duration DESC")
    List<Lesson> findLessonsByDurationDesc();
    
    // Find lessons by module with longest duration
    @Query("SELECT l FROM Lesson l " +
           "WHERE l.module.id = :moduleId " +
           "ORDER BY l.duration DESC")
    List<Lesson> findLessonsByDurationDescByModule(@Param("moduleId") Long moduleId);
    
    // Count lessons by course
    @Query("SELECT COUNT(l) FROM Lesson l " +
           "JOIN l.module m " +
           "WHERE m.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
} 