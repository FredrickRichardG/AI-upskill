package com.lms.enrollmentservice.controller;

import com.lms.enrollmentservice.dto.LessonProgressDto;
import com.lms.enrollmentservice.dto.ProgressAnalyticsDto;
import com.lms.enrollmentservice.service.ProgressTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Slf4j
public class ProgressController {
    
    private final ProgressTrackingService progressTrackingService;
    
    /**
     * Start a lesson for a user
     */
    @PostMapping("/lessons/{lessonId}/start")
    public ResponseEntity<LessonProgressDto> startLesson(
            @PathVariable Long lessonId,
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam Long enrollmentId) {
        
        log.info("Starting lesson {} for user {} in course {}", lessonId, userId, courseId);
        LessonProgressDto progress = progressTrackingService.startLesson(userId, lessonId, courseId, enrollmentId);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Update lesson progress
     */
    @PutMapping("/lessons/{lessonId}/progress")
    public ResponseEntity<LessonProgressDto> updateProgress(
            @PathVariable Long lessonId,
            @RequestParam Long userId,
            @RequestBody Map<String, Object> progressData) {
        
        Double completionPercentage = Double.valueOf(progressData.get("completionPercentage").toString());
        Integer timeSpent = Integer.valueOf(progressData.get("timeSpent").toString());
        
        log.info("Updating progress for lesson {} - user: {}, completion: {}%, time: {}s", 
                lessonId, userId, completionPercentage, timeSpent);
        
        LessonProgressDto progress = progressTrackingService.updateProgress(userId, lessonId, completionPercentage, timeSpent);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Complete a lesson
     */
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<LessonProgressDto> completeLesson(
            @PathVariable Long lessonId,
            @RequestParam Long userId) {
        
        log.info("Completing lesson {} for user {}", lessonId, userId);
        LessonProgressDto progress = progressTrackingService.completeLesson(userId, lessonId);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Get progress analytics for a user and course
     */
    @GetMapping("/analytics")
    public ResponseEntity<ProgressAnalyticsDto> getProgressAnalytics(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        
        log.info("Getting progress analytics for user {} in course {}", userId, courseId);
        ProgressAnalyticsDto analytics = progressTrackingService.getProgressAnalytics(userId, courseId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get all progress for a user in a course
     */
    @GetMapping("/course/{courseId}/user/{userId}")
    public ResponseEntity<List<LessonProgressDto>> getUserCourseProgress(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        
        log.info("Getting progress list for user {} in course {}", userId, courseId);
        List<LessonProgressDto> progressList = progressTrackingService.getUserCourseProgress(userId, courseId);
        return ResponseEntity.ok(progressList);
    }
    
    /**
     * Get recent progress for a user
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<LessonProgressDto>> getRecentProgress(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting recent progress for user {} with limit {}", userId, limit);
        List<LessonProgressDto> recentProgress = progressTrackingService.getRecentProgress(userId, limit);
        return ResponseEntity.ok(recentProgress);
    }
    
    /**
     * Get progress for a specific lesson
     */
    @GetMapping("/lessons/{lessonId}/user/{userId}")
    public ResponseEntity<LessonProgressDto> getLessonProgress(
            @PathVariable Long lessonId,
            @PathVariable Long userId) {
        
        log.info("Getting progress for lesson {} and user {}", lessonId, userId);
        // This would need to be implemented in the service
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get course completion status
     */
    @GetMapping("/course/{courseId}/user/{userId}/completion")
    public ResponseEntity<Map<String, Object>> getCourseCompletionStatus(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        
        log.info("Getting completion status for user {} in course {}", userId, courseId);
        ProgressAnalyticsDto analytics = progressTrackingService.getProgressAnalytics(userId, courseId);
        
        Map<String, Object> completionStatus = Map.of(
                "courseId", courseId,
                "userId", userId,
                "overallProgress", analytics.getOverallProgress(),
                "completedLessons", analytics.getCompletedLessons(),
                "totalLessons", analytics.getTotalLessons(),
                "isCompleted", analytics.getOverallProgress() >= 100.0
        );
        
        return ResponseEntity.ok(completionStatus);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "progress-tracking"));
    }
} 