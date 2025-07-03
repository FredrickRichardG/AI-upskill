package com.piggymetrics.enrollment.events;

public class EnrollmentCreatedEvent {
    private Long enrollmentId;
    private Long userId;
    private Long courseId;

    public EnrollmentCreatedEvent() {}
    public EnrollmentCreatedEvent(Long enrollmentId, Long userId, Long courseId) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
    }
    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
} 