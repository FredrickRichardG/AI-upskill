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
public class ModuleDto {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    private Long courseId;
    private List<LessonDto> lessons;
    private List<ResourceDto> resources;
    
    // Statistics
    private Long lessonCount;
    private Long resourceCount;
    private Integer totalDuration; // in minutes
} 