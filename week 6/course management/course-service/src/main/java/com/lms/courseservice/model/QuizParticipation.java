package com.lms.courseservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizParticipation {
    private String id;
    private Long quizId;
    private Long courseId;
    private Long userId;
    private String username;
    private ParticipationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer timeRemaining; // in seconds
    private Map<String, Object> answers;
    private Double score;
    private Boolean isCompleted;
    
    public enum ParticipationStatus {
        JOINED, STARTED, IN_PROGRESS, COMPLETED, TIMED_OUT
    }
} 