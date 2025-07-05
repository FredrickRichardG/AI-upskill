package com.lms.enrollmentservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressAnalyticsDto {
    private Long userId;
    private Long courseId;
    private String courseTitle;
    
    // Overall progress
    private Integer totalLessons;
    private Integer completedLessons;
    private Integer totalQuizzes;
    private Integer completedQuizzes;
    private Double overallProgress; // percentage
    private Double averageScore;
    
    // Time tracking
    private Long totalTimeSpent; // in seconds
    private Double averageTimePerLesson;
    private LocalDateTime firstAccessDate;
    private LocalDateTime lastAccessDate;
    private Long estimatedTimeToComplete; // in seconds
    
    // Learning patterns
    private List<String> preferredLearningTimes;
    private Map<String, Integer> dailyActivity;
    private Map<String, Double> weeklyProgress;
    
    // Assessment performance
    private Double quizAverageScore;
    private Integer totalQuizzesTaken;
    private Integer quizzesPassed;
    private Double assignmentAverageScore;
    
    // Engagement metrics
    private Integer streakDays;
    private Integer longestStreak;
    private Double engagementScore;
    
    // Course completion prediction
    private LocalDateTime estimatedCompletionDate;
    private Double completionProbability;
    
    // Recommendations
    private List<String> recommendedActions;
    private List<String> areasForImprovement;
} 