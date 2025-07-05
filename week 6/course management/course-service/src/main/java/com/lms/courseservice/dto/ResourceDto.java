package com.lms.courseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    private Long id;
    private String name;
    private String description;
    private String fileUrl;
    private String fileType;
    private String mimeType;
    private Long fileSize;
    private Boolean isPublic;
    private String downloadToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    private Long courseId;
    private Long moduleId;
    private Long lessonId;
    
    // Statistics
    private Long downloadCount;
    private Long viewCount;
} 