package com.studentresult.service.impl;

import com.studentresult.dto.StudentRequestDto;
import com.studentresult.dto.StudentResponseDto;
import com.studentresult.entity.Student;
import com.studentresult.exception.ResourceNotFoundException;
import com.studentresult.repository.StudentRepository;
import com.studentresult.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Student operations
 * Implements business logic for student management with Redis caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "'all'")
    public List<StudentResponseDto> getAllStudents() {
        log.info("Cache Miss → Loading all students from MySQL");
        List<Student> students = studentRepository.findAll();
        log.info("Found {} students from MySQL", students.size());
        return students.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "'student_' + #id")
    public StudentResponseDto getStudentById(Long id) {
        log.info("Cache Miss → Loading student with ID: {} from MySQL", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        log.info("Student found from MySQL: {}", student.getFullName());
        return convertToResponseDto(student);
    }
    
    @Override
    public StudentResponseDto createStudent(StudentRequestDto studentRequestDto) {
        log.info("Creating new student with hall ticket: {}", studentRequestDto.getHallTicketNo());
        
        // Check if hall ticket number already exists
        if (studentRepository.existsByHallTicketNo(studentRequestDto.getHallTicketNo())) {
            throw new IllegalArgumentException("Student with hall ticket number " + studentRequestDto.getHallTicketNo() + " already exists");
        }
        
        // Check if email already exists
        if (studentRepository.existsByEmail(studentRequestDto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + studentRequestDto.getEmail() + " already exists");
        }
        
        Student student = convertToEntity(studentRequestDto);
        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully with ID: {}", savedStudent.getStudentId());
        return convertToResponseDto(savedStudent);
    }
    
    @Override
    @Caching(
        put = @CachePut(value = "students", key = "'student_' + #id"),
        evict = @CacheEvict(value = "students", key = "'all'", allEntries = true)
    )
    public StudentResponseDto updateStudent(Long id, StudentRequestDto studentRequestDto) {
        log.info("Updating student with ID: {} - Cache Refresh", id);
        
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        
        // Check if hall ticket number is being changed and if it already exists
        if (!existingStudent.getHallTicketNo().equals(studentRequestDto.getHallTicketNo()) 
                && studentRepository.existsByHallTicketNo(studentRequestDto.getHallTicketNo())) {
            throw new IllegalArgumentException("Student with hall ticket number " + studentRequestDto.getHallTicketNo() + " already exists");
        }
        
        // Check if email is being changed and if it already exists
        if (!existingStudent.getEmail().equals(studentRequestDto.getEmail()) 
                && studentRepository.existsByEmail(studentRequestDto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + studentRequestDto.getEmail() + " already exists");
        }
        
        // Update student fields
        existingStudent.setHallTicketNo(studentRequestDto.getHallTicketNo());
        existingStudent.setFullName(studentRequestDto.getFullName());
        existingStudent.setGender(studentRequestDto.getGender());
        existingStudent.setDateOfBirth(studentRequestDto.getDateOfBirth());
        existingStudent.setEmail(studentRequestDto.getEmail());
        existingStudent.setPhone(studentRequestDto.getPhone());
        existingStudent.setDepartment(studentRequestDto.getDepartment());
        existingStudent.setYearOfStudy(studentRequestDto.getYearOfStudy());
        existingStudent.setSemester(studentRequestDto.getSemester());
        existingStudent.setSection(studentRequestDto.getSection());
        
        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Student updated successfully with ID: {} - Cache Refreshed", updatedStudent.getStudentId());
        return convertToResponseDto(updatedStudent);
    }
    
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "students", key = "'student_' + #id"),
            @CacheEvict(value = "students", key = "'all'", allEntries = true)
        }
    )
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {} - Cache Evict", id);
        
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        
        studentRepository.delete(student);
        log.info("Student deleted successfully with ID: {} - Cache Evicted", id);
    }
    
    /**
     * Convert Student entity to StudentResponseDto
     */
    private StudentResponseDto convertToResponseDto(Student student) {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setStudentId(student.getStudentId());
        dto.setHallTicketNo(student.getHallTicketNo());
        dto.setFullName(student.getFullName());
        dto.setGender(student.getGender());
        dto.setDateOfBirth(student.getDateOfBirth().format(DATE_FORMATTER));
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDepartment(student.getDepartment());
        dto.setYearOfStudy(student.getYearOfStudy());
        dto.setSemester(student.getSemester());
        dto.setSection(student.getSection());
        dto.setCreatedAt(student.getCreatedAt());
        return dto;
    }
    
    /**
     * Convert StudentRequestDto to Student entity
     */
    private Student convertToEntity(StudentRequestDto dto) {
        Student student = new Student();
        student.setHallTicketNo(dto.getHallTicketNo());
        student.setFullName(dto.getFullName());
        student.setGender(dto.getGender());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDepartment(dto.getDepartment());
        student.setYearOfStudy(dto.getYearOfStudy());
        student.setSemester(dto.getSemester());
        student.setSection(dto.getSection());
        return student;
    }
}
