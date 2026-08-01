package com.studentresult.controller;

import com.studentresult.dto.SubjectRequestDto;
import com.studentresult.dto.SubjectResponseDto;
import com.studentresult.service.SubjectService;
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
 * REST Controller for Subject operations
 * Provides CRUD endpoints for subject management
 */
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subject Management", description = "APIs for managing subject records")
public class SubjectController {
    
    private final SubjectService subjectService;
    
    @GetMapping
    @Operation(summary = "Get all subjects", description = "Retrieve a list of all subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all subjects"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        log.info("GET /api/subjects - Fetching all subjects");
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID", description = "Retrieve a subject by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved subject"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubjectResponseDto> getSubjectById(@PathVariable Long id) {
        log.info("GET /api/subjects/{} - Fetching subject by ID", id);
        SubjectResponseDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }
    
    @PostMapping
    @Operation(summary = "Create a new subject", description = "Create a new subject record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subject created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubjectResponseDto> createSubject(@Valid @RequestBody SubjectRequestDto subjectRequestDto) {
        log.info("POST /api/subjects - Creating new subject");
        SubjectResponseDto createdSubject = subjectService.createSubject(subjectRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a subject", description = "Update an existing subject record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subject updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SubjectResponseDto> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequestDto subjectRequestDto) {
        log.info("PUT /api/subjects/{} - Updating subject", id);
        SubjectResponseDto updatedSubject = subjectService.updateSubject(id, subjectRequestDto);
        return ResponseEntity.ok(updatedSubject);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subject", description = "Delete a subject record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        log.info("DELETE /api/subjects/{} - Deleting subject", id);
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
