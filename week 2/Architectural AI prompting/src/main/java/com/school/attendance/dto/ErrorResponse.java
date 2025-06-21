package com.school.attendance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String errorCode;
    private String message;
    private List<String> details;
    private String path;
} 