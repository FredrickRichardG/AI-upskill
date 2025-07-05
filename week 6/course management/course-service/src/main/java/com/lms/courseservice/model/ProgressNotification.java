package com.lms.courseservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressNotification {
    private String id;
    private Long userId;
    private Long courseId;
    private Long lessonId;
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private Boolean isRead;
    private Object data; // Additional data for specific notification types
    
    public enum NotificationType {
        LESSON_COMPLETED, QUIZ_PASSED, QUIZ_FAILED, ASSIGNMENT_DUE, 
        CERTIFICATE_EARNED, MILESTONE_REACHED, COURSE_COMPLETED, GENERAL
    }
} 