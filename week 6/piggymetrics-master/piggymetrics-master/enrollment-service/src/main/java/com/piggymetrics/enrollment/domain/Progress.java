package com.piggymetrics.enrollment.domain;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "completed_ts")
    private Instant completedTs;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public Instant getCompletedTs() { return completedTs; }
    public void setCompletedTs(Instant completedTs) { this.completedTs = completedTs; }
} 