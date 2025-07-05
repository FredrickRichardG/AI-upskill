package com.lms.enrollmentservice.repository;

import com.lms.enrollmentservice.model.LessonProgress;
import com.lms.enrollmentservice.model.ProgressStatus;
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
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    
    // Find by user and course
    List<LessonProgress> findByUserIdAndCourseId(Long userId, Long courseId);
    
    // Find by enrollment
    List<LessonProgress> findByEnrollmentId(Long enrollmentId);
    
    // Find by lesson
    List<LessonProgress> findByLessonId(Long lessonId);
    
    // Find by user and lesson
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);
    
    // Find by status
    List<LessonProgress> findByStatus(ProgressStatus status);
    List<LessonProgress> findByUserIdAndStatus(Long userId, ProgressStatus status);
    List<LessonProgress> findByCourseIdAndStatus(Long courseId, ProgressStatus status);
    
    // Find completed lessons
    List<LessonProgress> findByUserIdAndIsCompletedTrue(Long userId);
    List<LessonProgress> findByCourseIdAndIsCompletedTrue(Long courseId);
    
    // Analytics queries
    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.userId = :userId AND lp.courseId = :courseId")
    long countByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.userId = :userId AND lp.courseId = :courseId AND lp.isCompleted = true")
    long countCompletedByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT AVG(lp.completionPercentage) FROM LessonProgress lp WHERE lp.userId = :userId AND lp.courseId = :courseId")
    Double getAverageCompletionByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT AVG(lp.timeSpent) FROM LessonProgress lp WHERE lp.userId = :userId AND lp.courseId = :courseId")
    Double getAverageTimeSpentByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    // Find recent progress
    List<LessonProgress> findByUserIdOrderByLastAccessedAtDesc(Long userId);
    List<LessonProgress> findByCourseIdOrderByLastAccessedAtDesc(Long courseId);
    
    // Find progress by date range
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.userId = :userId AND lp.lastAccessedAt BETWEEN :startDate AND :endDate")
    List<LessonProgress> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    // Find lessons in progress
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.userId = :userId AND lp.status = 'IN_PROGRESS'")
    List<LessonProgress> findInProgressByUser(@Param("userId") Long userId);
    
    // Find lessons not started
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.userId = :userId AND lp.status = 'NOT_STARTED'")
    List<LessonProgress> findNotStartedByUser(@Param("userId") Long userId);
    
    // Pagination
    Page<LessonProgress> findByUserId(Long userId, Pageable pageable);
    Page<LessonProgress> findByCourseId(Long courseId, Pageable pageable);
    
    // Find by completion percentage range
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.userId = :userId AND lp.completionPercentage BETWEEN :min AND :max")
    List<LessonProgress> findByCompletionPercentageRange(@Param("userId") Long userId, 
                                                       @Param("min") Double min, 
                                                       @Param("max") Double max);
} 