package com.studentresult.controller;

import com.studentresult.dto.ResultRequestDto;
import com.studentresult.dto.ResultResponseDto;
import com.studentresult.service.ResultService;
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
 * REST Controller for Result operations
 * Provides CRUD endpoints for result management
 * Automatically calculates totalMarks, grade, and resultStatus
 */
@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Result Management", description = "APIs for managing student results")
public class ResultController {
    
    private final ResultService resultService;
    
    @GetMapping
    @Operation(summary = "Get all results", description = "Retrieve a list of all results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all results"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ResultResponseDto>> getAllResults() {
        log.info("GET /api/results - Fetching all results");
        List<ResultResponseDto> results = resultService.getAllResults();
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get result by ID", description = "Retrieve a result by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved result"),
            @ApiResponse(responseCode = "404", description = "Result not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResultResponseDto> getResultById(@PathVariable Long id) {
        log.info("GET /api/results/{} - Fetching result by ID", id);
        ResultResponseDto result = resultService.getResultById(id);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @Operation(summary = "Create a new result", description = "Create a new result record. Total marks, grade, and status are calculated automatically.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Result created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Student or subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResultResponseDto> createResult(@Valid @RequestBody ResultRequestDto resultRequestDto) {
        log.info("POST /api/results - Creating new result");
        ResultResponseDto createdResult = resultService.createResult(resultRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResult);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a result", description = "Update an existing result record. Total marks, grade, and status are recalculated automatically.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Result, student, or subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResultResponseDto> updateResult(
            @PathVariable Long id,
            @Valid @RequestBody ResultRequestDto resultRequestDto) {
        log.info("PUT /api/results/{} - Updating result", id);
        ResultResponseDto updatedResult = resultService.updateResult(id, resultRequestDto);
        return ResponseEntity.ok(updatedResult);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a result", description = "Delete a result record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Result deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Result not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        log.info("DELETE /api/results/{} - Deleting result", id);
        resultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }
}
