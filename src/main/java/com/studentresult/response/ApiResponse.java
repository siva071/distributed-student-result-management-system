package com.studentresult.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API Response wrapper class
 * Provides consistent response structure for all API endpoints
 * @param <T> The type of data being returned
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    // Metadata for load balancing tracking
    private String servedBy;
    private String instance;
    private String hostname;
    private Long responseTime;
    
    /**
     * Add metadata for load balancing tracking
     */
    public ApiResponse<T> withMetadata(String port, String instance, String hostname, Long responseTime) {
        this.servedBy = port;
        this.instance = instance;
        this.hostname = hostname;
        this.responseTime = responseTime;
        return this;
    }
    
    /**
     * Create a success response
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Success");
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * Create a success response with custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
