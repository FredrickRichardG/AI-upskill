package com.piggymetrics.course.events;

public class CourseArchivedEvent {
    private Long courseId;

    public CourseArchivedEvent() {}
    public CourseArchivedEvent(Long courseId) { this.courseId = courseId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
} 