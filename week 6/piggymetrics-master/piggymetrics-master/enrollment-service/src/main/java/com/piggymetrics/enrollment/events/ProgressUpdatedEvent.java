package com.piggymetrics.enrollment.events;

public class ProgressUpdatedEvent {
    private Long userId;
    private Long courseId;
    private Double progressPct;

    public ProgressUpdatedEvent() {}
    public ProgressUpdatedEvent(Long userId, Long courseId, Double progressPct) {
        this.userId = userId;
        this.courseId = courseId;
        this.progressPct = progressPct;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Double getProgressPct() { return progressPct; }
    public void setProgressPct(Double progressPct) { this.progressPct = progressPct; }
} 