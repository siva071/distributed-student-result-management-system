package com.studentresult.service;

import com.studentresult.dto.StudentRequestDto;
import com.studentresult.dto.StudentResponseDto;

import java.util.List;

/**
 * Service interface for Student operations
 * Defines business logic methods for student management
 */
public interface StudentService {
    
    /**
     * Get all students
     * @return List of all students
     */
    List<StudentResponseDto> getAllStudents();
    
    /**
     * Get student by ID
     * @param id the student ID
     * @return Student response DTO
     */
    StudentResponseDto getStudentById(Long id);
    
    /**
     * Create a new student
     * @param studentRequestDto the student request DTO
     * @return Created student response DTO
     */
    StudentResponseDto createStudent(StudentRequestDto studentRequestDto);
    
    /**
     * Update an existing student
     * @param id the student ID
     * @param studentRequestDto the student request DTO
     * @return Updated student response DTO
     */
    StudentResponseDto updateStudent(Long id, StudentRequestDto studentRequestDto);
    
    /**
     * Delete a student by ID
     * @param id the student ID
     */
    void deleteStudent(Long id);
}
