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
public class InstructorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String profileImageUrl;
    private String expertise;
    private String qualification;
    private Integer yearsOfExperience;
    private String linkedinUrl;
    private String twitterUrl;
    private String websiteUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Statistics
    private Long courseCount;
    private Long publishedCourseCount;
    private Double averageRating;
    private Long totalStudents;
} 