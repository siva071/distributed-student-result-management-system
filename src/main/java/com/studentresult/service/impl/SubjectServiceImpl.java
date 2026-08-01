package com.studentresult.service.impl;

import com.studentresult.dto.SubjectRequestDto;
import com.studentresult.dto.SubjectResponseDto;
import com.studentresult.entity.Subject;
import com.studentresult.exception.ResourceNotFoundException;
import com.studentresult.repository.SubjectRepository;
import com.studentresult.service.SubjectService;
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
 * Service implementation for Subject operations
 * Implements business logic for subject management with Redis caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectServiceImpl implements SubjectService {
    
    private final SubjectRepository subjectRepository;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "subjects", key = "'all'")
    public List<SubjectResponseDto> getAllSubjects() {
        log.info("Cache Miss → Loading all subjects from MySQL");
        List<Subject> subjects = subjectRepository.findAll();
        log.info("Found {} subjects from MySQL", subjects.size());
        return subjects.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "subjects", key = "'subject_' + #id")
    public SubjectResponseDto getSubjectById(Long id) {
        log.info("Cache Miss → Loading subject with ID: {} from MySQL", id);
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        log.info("Subject found from MySQL: {}", subject.getSubjectName());
        return convertToResponseDto(subject);
    }
    
    @Override
    public SubjectResponseDto createSubject(SubjectRequestDto subjectRequestDto) {
        log.info("Creating new subject with code: {}", subjectRequestDto.getSubjectCode());
        
        // Check if subject code already exists
        if (subjectRepository.existsBySubjectCode(subjectRequestDto.getSubjectCode())) {
            throw new IllegalArgumentException("Subject with code " + subjectRequestDto.getSubjectCode() + " already exists");
        }
        
        Subject subject = convertToEntity(subjectRequestDto);
        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject created successfully with ID: {}", savedSubject.getSubjectId());
        return convertToResponseDto(savedSubject);
    }
    
    @Override
    @Caching(
        put = @CachePut(value = "subjects", key = "'subject_' + #id"),
        evict = @CacheEvict(value = "subjects", key = "'all'", allEntries = true)
    )
    public SubjectResponseDto updateSubject(Long id, SubjectRequestDto subjectRequestDto) {
        log.info("Updating subject with ID: {} - Cache Refresh", id);
        
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        
        // Check if subject code is being changed and if it already exists
        if (!existingSubject.getSubjectCode().equals(subjectRequestDto.getSubjectCode()) 
                && subjectRepository.existsBySubjectCode(subjectRequestDto.getSubjectCode())) {
            throw new IllegalArgumentException("Subject with code " + subjectRequestDto.getSubjectCode() + " already exists");
        }
        
        // Update subject fields
        existingSubject.setSubjectCode(subjectRequestDto.getSubjectCode());
        existingSubject.setSubjectName(subjectRequestDto.getSubjectName());
        existingSubject.setDepartment(subjectRequestDto.getDepartment());
        existingSubject.setSemester(subjectRequestDto.getSemester());
        existingSubject.setCredits(subjectRequestDto.getCredits());
        
        Subject updatedSubject = subjectRepository.save(existingSubject);
        log.info("Subject updated successfully with ID: {} - Cache Refreshed", updatedSubject.getSubjectId());
        return convertToResponseDto(updatedSubject);
    }
    
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "subjects", key = "'subject_' + #id"),
            @CacheEvict(value = "subjects", key = "'all'", allEntries = true)
        }
    )
    public void deleteSubject(Long id) {
        log.info("Deleting subject with ID: {} - Cache Evict", id);
        
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        
        subjectRepository.delete(subject);
        log.info("Subject deleted successfully with ID: {} - Cache Evicted", id);
    }
    
    /**
     * Convert Subject entity to SubjectResponseDto
     */
    private SubjectResponseDto convertToResponseDto(Subject subject) {
        SubjectResponseDto dto = new SubjectResponseDto();
        dto.setSubjectId(subject.getSubjectId());
        dto.setSubjectCode(subject.getSubjectCode());
        dto.setSubjectName(subject.getSubjectName());
        dto.setDepartment(subject.getDepartment());
        dto.setSemester(subject.getSemester());
        dto.setCredits(subject.getCredits());
        return dto;
    }
    
    /**
     * Convert SubjectRequestDto to Subject entity
     */
    private Subject convertToEntity(SubjectRequestDto dto) {
        Subject subject = new Subject();
        subject.setSubjectCode(dto.getSubjectCode());
        subject.setSubjectName(dto.getSubjectName());
        subject.setDepartment(dto.getDepartment());
        subject.setSemester(dto.getSemester());
        subject.setCredits(dto.getCredits());
        return subject;
    }
}
