package com.piggymetrics.assessment.events;

public class GradeAwardedEvent {
    private Long userId;
    private Long courseId;
    private Double grade;

    public GradeAwardedEvent() {}
    public GradeAwardedEvent(Long userId, Long courseId, Double grade) {
        this.userId = userId;
        this.courseId = courseId;
        this.grade = grade;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }
} 