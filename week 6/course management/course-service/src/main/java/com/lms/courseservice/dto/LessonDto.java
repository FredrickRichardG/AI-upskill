package com.lms.courseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String videoUrl;
    private Integer duration; // in minutes
    private Integer orderIndex;
    private Boolean isPublished;
    private String transcript;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    private Long moduleId;
    private List<ResourceDto> resources;
    
    // Statistics
    private Long resourceCount;
    private Long viewCount;
    private Long completionCount;
} 