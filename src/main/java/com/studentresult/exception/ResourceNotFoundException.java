package com.studentresult.exception;

/**
 * Custom exception thrown when a requested resource is not found
 * Used when an entity with the given ID does not exist
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
