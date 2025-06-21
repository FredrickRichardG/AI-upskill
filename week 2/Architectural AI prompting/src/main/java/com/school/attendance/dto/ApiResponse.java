package com.school.attendance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * Standardized API response wrapper for all endpoints.
 *
 * @param <T> The type of data being returned
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Object details;

    {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a success response with data.
     *
     * @param data The data to be returned
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setMessage("Success");
        return response;
    }

    /**
     * Creates a success response with data and message.
     *
     * @param data The data to be returned
     * @param message The success message
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    /**
     * Creates an error response.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        response.setErrorCode(errorCode);
        return response;
    }

    /**
     * Creates an error response with details.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param details The error details
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, Object details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setDetails(details);
        return response;
    }

    /**
     * Creates a paginated response.
     *
     * @param page The page to be returned
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<Page<T>> paginated(Page<T> page) {
        ApiResponse<Page<T>> response = new ApiResponse<>();
        response.setData(page);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setMessage("Success");
        return response;
    }

    /**
     * Creates a paginated response with a custom message.
     *
     * @param page The page to be returned
     * @param message The custom message
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<Page<T>> paginated(Page<T> page, String message) {
        ApiResponse<Page<T>> response = new ApiResponse<>();
        response.setData(page);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setMessage(message);
        return response;
    }
} 