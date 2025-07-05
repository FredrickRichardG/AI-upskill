package com.lms.courseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {
    private String title;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private String difficulty;
    private Integer duration;
    private String thumbnailUrl;
    private String previewVideoUrl;
    private Boolean isFeatured;
    private Boolean isPublic;
    private Integer maxStudents;
    private String prerequisites;
    private String learningObjectives;
    private String targetAudience;
    private String certificateTemplate;
    
    // Relationships
    private Long categoryId;
    private Set<Long> tagIds;
    private List<Long> instructorIds;
} 