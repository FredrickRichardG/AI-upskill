package com.lms.enrollmentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long enrollmentId;

    @Column(nullable = false)
    private Long lessonId;

    @Column(nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;

    // Time tracking
    private Integer timeSpent; // in seconds
    private Integer totalDuration; // in minutes

    // Completion tracking
    private Boolean isCompleted = false;
    private Double completionPercentage;
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ProgressStatus.NOT_STARTED;
        }
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        lastAccessedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastAccessedAt = LocalDateTime.now();
    }
} 