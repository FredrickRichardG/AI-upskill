package com.lms.assessmentservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(nullable = false)
    private Integer attemptNumber;

    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime gradedAt;

    private String gradedBy;

    // Alias for gradedAt to match controller usage
    public void setGradedTs(Instant gradedTs) {
        this.gradedAt = gradedTs != null ? gradedTs.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    // Progress tracking
    private Integer timeSpent; // in seconds
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Integer skippedQuestions;

    // Detailed analytics
    private String answers; // JSON string of question-answer pairs
    private String feedback;
    private Boolean isPassed;
    private Double percentageScore;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (status == null) {
            status = SubmissionStatus.SUBMITTED;
        }
    }
} 