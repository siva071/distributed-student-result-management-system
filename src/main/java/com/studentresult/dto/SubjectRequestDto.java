package com.studentresult.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Subject creation and update requests
 * Contains validation annotations for data integrity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequestDto {
    
    @NotBlank(message = "Subject code cannot be null")
    private String subjectCode;
    
    @NotBlank(message = "Subject name cannot be null")
    private String subjectName;
    
    @NotBlank(message = "Department cannot be null")
    private String department;
    
    @NotNull(message = "Semester cannot be null")
    private Integer semester;
    
    @NotNull(message = "Credits cannot be null")
    @Positive(message = "Credits must be greater than zero")
    private Integer credits;
}
