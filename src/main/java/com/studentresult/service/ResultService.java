package com.studentresult.service;

import com.studentresult.dto.ResultRequestDto;
import com.studentresult.dto.ResultResponseDto;

import java.util.List;

/**
 * Service interface for Result operations
 * Defines business logic methods for result management
 */
public interface ResultService {
    
    /**
     * Get all results
     * @return List of all results
     */
    List<ResultResponseDto> getAllResults();
    
    /**
     * Get result by ID
     * @param id the result ID
     * @return Result response DTO
     */
    ResultResponseDto getResultById(Long id);
    
    /**
     * Create a new result
     * @param resultRequestDto the result request DTO
     * @return Created result response DTO
     */
    ResultResponseDto createResult(ResultRequestDto resultRequestDto);
    
    /**
     * Update an existing result
     * @param id the result ID
     * @param resultRequestDto the result request DTO
     * @return Updated result response DTO
     */
    ResultResponseDto updateResult(Long id, ResultRequestDto resultRequestDto);
    
    /**
     * Delete a result by ID
     * @param id the result ID
     */
    void deleteResult(Long id);
}
