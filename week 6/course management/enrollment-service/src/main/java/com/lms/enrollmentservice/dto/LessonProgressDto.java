package com.lms.enrollmentservice.dto;

import com.lms.enrollmentservice.model.ProgressStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressDto {
    private Long id;
    private Long userId;
    private Long enrollmentId;
    private Long lessonId;
    private Long courseId;
    private ProgressStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
    private Integer timeSpent; // in seconds
    private Integer totalDuration; // in minutes
    private Boolean isCompleted;
    private Double completionPercentage;
    private String notes;
    
    // Additional fields for UI
    private String lessonTitle;
    private String courseTitle;
    private String moduleTitle;
} 