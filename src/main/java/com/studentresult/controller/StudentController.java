package com.studentresult.controller;

import com.studentresult.dto.StudentRequestDto;
import com.studentresult.dto.StudentResponseDto;
import com.studentresult.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Student operations
 * Provides CRUD endpoints for student management
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Management", description = "APIs for managing student records")
public class StudentController {
    
    private final StudentService studentService;
    
    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve a list of all students")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all students"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        log.info("GET /api/students - Fetching all students");
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieve a student by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved student"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        log.info("GET /api/students/{} - Fetching student by ID", id);
        StudentResponseDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }
    
    @PostMapping
    @Operation(summary = "Create a new student", description = "Create a new student record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StudentResponseDto> createStudent(@Valid @RequestBody StudentRequestDto studentRequestDto) {
        log.info("POST /api/students - Creating new student");
        StudentResponseDto createdStudent = studentService.createStudent(studentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a student", description = "Update an existing student record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto studentRequestDto) {
        log.info("PUT /api/students/{} - Updating student", id);
        StudentResponseDto updatedStudent = studentService.updateStudent(id, studentRequestDto);
        return ResponseEntity.ok(updatedStudent);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Delete a student record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("DELETE /api/students/{} - Deleting student", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
