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
public class ChatMessage {
    private String id;
    private Long courseId;
    private Long lessonId;
    private Long userId;
    private String username;
    private String message;
    private MessageType type;
    private LocalDateTime timestamp;
    
    public enum MessageType {
        CHAT, JOIN, LEAVE, ANNOUNCEMENT
    }
} 