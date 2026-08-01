package com.studentresult.service.impl;

import com.studentresult.dto.ResultRequestDto;
import com.studentresult.dto.ResultResponseDto;
import com.studentresult.entity.Result;
import com.studentresult.entity.Student;
import com.studentresult.entity.Subject;
import com.studentresult.exception.ResourceNotFoundException;
import com.studentresult.repository.ResultRepository;
import com.studentresult.repository.StudentRepository;
import com.studentresult.repository.SubjectRepository;
import com.studentresult.service.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Result operations
 * Implements business logic for result management with Redis caching
 * Automatically calculates totalMarks, grade, and resultStatus
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResultServiceImpl implements ResultService {
    
    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "results", key = "'all'")
    public List<ResultResponseDto> getAllResults() {
        log.info("Cache Miss → Loading all results from MySQL");
        List<Result> results = resultRepository.findAll();
        log.info("Found {} results from MySQL", results.size());
        return results.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "results", key = "'result_' + #id")
    public ResultResponseDto getResultById(Long id) {
        log.info("Cache Miss → Loading result with ID: {} from MySQL", id);
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with ID: " + id));
        log.info("Result found from MySQL for student: {}", result.getStudent().getFullName());
        return convertToResponseDto(result);
    }
    
    @Override
    public ResultResponseDto createResult(ResultRequestDto resultRequestDto) {
        log.info("Creating new result for student ID: {} and subject ID: {}", 
                resultRequestDto.getStudentId(), resultRequestDto.getSubjectId());
        
        // Validate student exists
        Student student = studentRepository.findById(resultRequestDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + resultRequestDto.getStudentId()));
        
        // Validate subject exists
        Subject subject = subjectRepository.findById(resultRequestDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + resultRequestDto.getSubjectId()));
        
        // Check if result already exists for this student and subject
        resultRepository.findByStudentStudentIdAndSubjectSubjectId(
                resultRequestDto.getStudentId(), resultRequestDto.getSubjectId())
                .ifPresent(existingResult -> {
                    throw new IllegalArgumentException("Result already exists for this student and subject");
                });
        
        Result result = new Result();
        result.setStudent(student);
        result.setSubject(subject);
        result.setInternalMarks(resultRequestDto.getInternalMarks());
        result.setExternalMarks(resultRequestDto.getExternalMarks());
        
        // totalMarks, grade, and resultStatus are automatically calculated in @PrePersist
        
        Result savedResult = resultRepository.save(result);
        log.info("Result created successfully with ID: {}", savedResult.getResultId());
        return convertToResponseDto(savedResult);
    }
    
    @Override
    @Caching(
        put = @CachePut(value = "results", key = "'result_' + #id"),
        evict = @CacheEvict(value = "results", key = "'all'", allEntries = true)
    )
    public ResultResponseDto updateResult(Long id, ResultRequestDto resultRequestDto) {
        log.info("Updating result with ID: {} - Cache Refresh", id);
        
        Result existingResult = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with ID: " + id));
        
        // Validate student exists if studentId is being changed
        if (!existingResult.getStudent().getStudentId().equals(resultRequestDto.getStudentId())) {
            Student student = studentRepository.findById(resultRequestDto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + resultRequestDto.getStudentId()));
            existingResult.setStudent(student);
        }
        
        // Validate subject exists if subjectId is being changed
        if (!existingResult.getSubject().getSubjectId().equals(resultRequestDto.getSubjectId())) {
            Subject subject = subjectRepository.findById(resultRequestDto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + resultRequestDto.getSubjectId()));
            existingResult.setSubject(subject);
        }
        
        // Update marks
        existingResult.setInternalMarks(resultRequestDto.getInternalMarks());
        existingResult.setExternalMarks(resultRequestDto.getExternalMarks());
        
        // totalMarks, grade, and resultStatus are automatically recalculated in @PreUpdate
        
        Result updatedResult = resultRepository.save(existingResult);
        log.info("Result updated successfully with ID: {} - Cache Refreshed", updatedResult.getResultId());
        return convertToResponseDto(updatedResult);
    }
    
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "results", key = "'result_' + #id"),
            @CacheEvict(value = "results", key = "'all'", allEntries = true)
        }
    )
    public void deleteResult(Long id) {
        log.info("Deleting result with ID: {} - Cache Evict", id);
        
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with ID: " + id));
        
        resultRepository.delete(result);
        log.info("Result deleted successfully with ID: {} - Cache Evicted", id);
    }
    
    /**
     * Convert Result entity to ResultResponseDto
     * Includes student and subject information
     */
    private ResultResponseDto convertToResponseDto(Result result) {
        ResultResponseDto dto = new ResultResponseDto();
        dto.setResultId(result.getResultId());
        dto.setStudentId(result.getStudent().getStudentId());
        dto.setStudentName(result.getStudent().getFullName());
        dto.setSubjectId(result.getSubject().getSubjectId());
        dto.setSubjectName(result.getSubject().getSubjectName());
        dto.setInternalMarks(result.getInternalMarks());
        dto.setExternalMarks(result.getExternalMarks());
        dto.setTotalMarks(result.getTotalMarks());
        dto.setGrade(result.getGrade());
        dto.setResultStatus(result.getResultStatus());
        return dto;
    }
}
