package com.lms.courseservice.dto;

import com.lms.courseservice.model.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchRequest {
    private String keyword;
    private Long categoryId;
    private List<Long> tagIds;
    private List<Long> instructorIds;
    private CourseStatus status;
    private String difficulty;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minDuration;
    private Integer maxDuration;
    private Boolean isFeatured;
    private Boolean isPublic;
    private Pageable pageable;
} 