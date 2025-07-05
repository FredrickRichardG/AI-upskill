package com.lms.courseservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private String id;
    private Long courseId;
    private Long instructorId;
    private String instructorName;
    private String title;
    private String content;
    private AnnouncementType type;
    private LocalDateTime timestamp;
    private Boolean isUrgent;
    
    public enum AnnouncementType {
        GENERAL, ASSIGNMENT, QUIZ, REMINDER, URGENT
    }
} 