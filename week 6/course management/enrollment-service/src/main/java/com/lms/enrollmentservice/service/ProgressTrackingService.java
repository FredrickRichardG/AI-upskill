package com.lms.enrollmentservice.service;

import com.lms.enrollmentservice.dto.LessonProgressDto;
import com.lms.enrollmentservice.dto.ProgressAnalyticsDto;
import com.lms.enrollmentservice.model.LessonProgress;
import com.lms.enrollmentservice.model.ProgressStatus;
import com.lms.enrollmentservice.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressTrackingService {
    
    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentService enrollmentService;
    private final NotificationService notificationService;
    
    /**
     * Start a lesson for a user
     */
    @Transactional
    @CacheEvict(value = "progress", key = "#userId + '_' + #courseId")
    public LessonProgressDto startLesson(Long userId, Long lessonId, Long courseId, Long enrollmentId) {
        log.info("Starting lesson {} for user {} in course {}", lessonId, userId, courseId);
        
        Optional<LessonProgress> existingProgress = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId);
        
        if (existingProgress.isPresent()) {
            LessonProgress progress = existingProgress.get();
            if (progress.getStatus() == ProgressStatus.NOT_STARTED) {
                progress.setStatus(ProgressStatus.IN_PROGRESS);
                progress.setStartedAt(LocalDateTime.now());
                progress.setLastAccessedAt(LocalDateTime.now());
                return mapToDto(lessonProgressRepository.save(progress));
            }
            return mapToDto(progress);
        }
        
        LessonProgress newProgress = LessonProgress.builder()
                .userId(userId)
                .lessonId(lessonId)
                .courseId(courseId)
                .enrollmentId(enrollmentId)
                .status(ProgressStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .isCompleted(false)
                .completionPercentage(0.0)
                .build();
        
        LessonProgress saved = lessonProgressRepository.save(newProgress);
        
        // Async event for lesson start
        notifyLessonStarted(userId, lessonId, courseId);
        
        return mapToDto(saved);
    }
    
    /**
     * Update lesson progress
     */
    @Transactional
    @CacheEvict(value = "progress", key = "#userId + '_' + #courseId")
    public LessonProgressDto updateProgress(Long userId, Long lessonId, Double completionPercentage, Integer timeSpent) {
        log.info("Updating progress for lesson {} - user: {}, completion: {}%, time: {}s", 
                lessonId, userId, completionPercentage, timeSpent);
        
        Optional<LessonProgress> progressOpt = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId);
        
        if (progressOpt.isEmpty()) {
            throw new RuntimeException("Progress not found for user " + userId + " and lesson " + lessonId);
        }
        
        LessonProgress progress = progressOpt.get();
        progress.setCompletionPercentage(completionPercentage);
        progress.setTimeSpent(timeSpent);
        progress.setLastAccessedAt(LocalDateTime.now());
        
        // Check if lesson is completed
        if (completionPercentage >= 100.0 && !progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
            
            // Async event for lesson completion
            notifyLessonCompleted(userId, lessonId, progress.getCourseId());
        }
        
        LessonProgress saved = lessonProgressRepository.save(progress);
        return mapToDto(saved);
    }
    
    /**
     * Complete a lesson
     */
    @Transactional
    @CacheEvict(value = "progress", key = "#userId + '_' + #courseId")
    public LessonProgressDto completeLesson(Long userId, Long lessonId) {
        log.info("Completing lesson {} for user {}", lessonId, userId);
        
        Optional<LessonProgress> progressOpt = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId);
        
        if (progressOpt.isEmpty()) {
            throw new RuntimeException("Progress not found for user " + userId + " and lesson " + lessonId);
        }
        
        LessonProgress progress = progressOpt.get();
        progress.setIsCompleted(true);
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setCompletionPercentage(100.0);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setLastAccessedAt(LocalDateTime.now());
        
        LessonProgress saved = lessonProgressRepository.save(progress);
        
        // Async event for lesson completion
        notifyLessonCompleted(userId, lessonId, progress.getCourseId());
        
        return mapToDto(saved);
    }
    
    /**
     * Get progress analytics for a user and course
     */
    @Cacheable(value = "progress", key = "#userId + '_' + #courseId + '_analytics'")
    public ProgressAnalyticsDto getProgressAnalytics(Long userId, Long courseId) {
        log.info("Getting progress analytics for user {} in course {}", userId, courseId);
        
        List<LessonProgress> allProgress = lessonProgressRepository.findByUserIdAndCourseId(userId, courseId);
        
        int totalLessons = allProgress.size();
        int completedLessons = (int) allProgress.stream()
                .filter(LessonProgress::getIsCompleted)
                .count();
        
        double overallProgress = totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0.0;
        
        double averageCompletion = allProgress.stream()
                .mapToDouble(LessonProgress::getCompletionPercentage)
                .average()
                .orElse(0.0);
        
        long totalTimeSpent = allProgress.stream()
                .mapToLong(progress -> progress.getTimeSpent() != null ? progress.getTimeSpent() : 0)
                .sum();
        
        double averageTimePerLesson = allProgress.stream()
                .filter(progress -> progress.getTimeSpent() != null)
                .mapToDouble(progress -> progress.getTimeSpent())
                .average()
                .orElse(0.0);
        
        LocalDateTime firstAccess = allProgress.stream()
                .map(LessonProgress::getStartedAt)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        
        LocalDateTime lastAccess = allProgress.stream()
                .map(LessonProgress::getLastAccessedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        return ProgressAnalyticsDto.builder()
                .userId(userId)
                .courseId(courseId)
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .overallProgress(overallProgress)
                .averageScore(averageCompletion)
                .totalTimeSpent(totalTimeSpent)
                .averageTimePerLesson(averageTimePerLesson)
                .firstAccessDate(firstAccess)
                .lastAccessDate(lastAccess)
                .build();
    }
    
    /**
     * Get all progress for a user in a course
     */
    @Cacheable(value = "progress", key = "#userId + '_' + #courseId + '_list'")
    public List<LessonProgressDto> getUserCourseProgress(Long userId, Long courseId) {
        log.info("Getting progress list for user {} in course {}", userId, courseId);
        
        List<LessonProgress> progressList = lessonProgressRepository.findByUserIdAndCourseId(userId, courseId);
        return progressList.stream()
                .map(this::mapToDto)
                .toList();
    }
    
    /**
     * Get recent progress for a user
     */
    public List<LessonProgressDto> getRecentProgress(Long userId, int limit) {
        List<LessonProgress> recentProgress = lessonProgressRepository.findByUserIdOrderByLastAccessedAtDesc(userId);
        return recentProgress.stream()
                .limit(limit)
                .map(this::mapToDto)
                .toList();
    }
    
    /**
     * Async notification for lesson start
     */
    @Async
    public CompletableFuture<Void> notifyLessonStarted(Long userId, Long lessonId, Long courseId) {
        log.info("Sending lesson started notification for user {} lesson {}", userId, lessonId);
        // TODO: Implement notification logic
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Async notification for lesson completion
     */
    @Async
    public CompletableFuture<Void> notifyLessonCompleted(Long userId, Long lessonId, Long courseId) {
        log.info("Sending lesson completed notification for user {} lesson {}", userId, lessonId);
        // TODO: Implement notification logic
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Map entity to DTO
     */
    private LessonProgressDto mapToDto(LessonProgress progress) {
        return LessonProgressDto.builder()
                .id(progress.getId())
                .userId(progress.getUserId())
                .enrollmentId(progress.getEnrollmentId())
                .lessonId(progress.getLessonId())
                .courseId(progress.getCourseId())
                .status(progress.getStatus())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .lastAccessedAt(progress.getLastAccessedAt())
                .timeSpent(progress.getTimeSpent())
                .totalDuration(progress.getTotalDuration())
                .isCompleted(progress.getIsCompleted())
                .completionPercentage(progress.getCompletionPercentage())
                .notes(progress.getNotes())
                .build();
    }
} 