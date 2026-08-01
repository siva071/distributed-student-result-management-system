package com.studentresult.service;

import com.studentresult.dto.SubjectRequestDto;
import com.studentresult.dto.SubjectResponseDto;

import java.util.List;

/**
 * Service interface for Subject operations
 * Defines business logic methods for subject management
 */
public interface SubjectService {
    
    /**
     * Get all subjects
     * @return List of all subjects
     */
    List<SubjectResponseDto> getAllSubjects();
    
    /**
     * Get subject by ID
     * @param id the subject ID
     * @return Subject response DTO
     */
    SubjectResponseDto getSubjectById(Long id);
    
    /**
     * Create a new subject
     * @param subjectRequestDto the subject request DTO
     * @return Created subject response DTO
     */
    SubjectResponseDto createSubject(SubjectRequestDto subjectRequestDto);
    
    /**
     * Update an existing subject
     * @param id the subject ID
     * @param subjectRequestDto the subject request DTO
     * @return Updated subject response DTO
     */
    SubjectResponseDto updateSubject(Long id, SubjectRequestDto subjectRequestDto);
    
    /**
     * Delete a subject by ID
     * @param id the subject ID
     */
    void deleteSubject(Long id);
}
