package com.piggymetrics.assessment.domain;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Double score;
    private String status;

    @Column(name = "graded_ts")
    private Instant gradedTs;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getGradedTs() { return gradedTs; }
    public void setGradedTs(Instant gradedTs) { this.gradedTs = gradedTs; }
} 