package com.lms.courseservice.dto;

import com.lms.courseservice.model.VersionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentVersionDto {
    private Long id;
    private String version;
    private String changeLog;
    private VersionStatus status;
    private String createdBy;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    private Long courseId;
} 