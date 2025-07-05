package com.lms.courseservice.dto;

import com.lms.courseservice.model.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private String difficulty;
    private Integer duration; // in minutes
    private String thumbnailUrl;
    private String previewVideoUrl;
    private CourseStatus status;
    private Boolean isFeatured;
    private Boolean isPublic;
    private Integer maxStudents;
    private String prerequisites;
    private String learningObjectives;
    private String targetAudience;
    private String certificateTemplate;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    private CategoryDto category;
    private Set<TagDto> tags;
    private List<InstructorDto> instructors;
    private List<ModuleDto> modules;
    private ContentVersionDto latestVersion;
    
    // Statistics
    private Long totalLessons;
    private Long totalModules;
    private Long totalResources;
    private Long enrolledStudents;
    private Double averageRating;
    private Long totalReviews;
} 