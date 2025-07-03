package com.piggymetrics.course.events;

public class CourseCreatedEvent {
    private Long courseId;
    private String title;
    private String authorId;

    public CourseCreatedEvent() {}
    public CourseCreatedEvent(Long courseId, String title, String authorId) {
        this.courseId = courseId;
        this.title = title;
        this.authorId = authorId;
    }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
} 