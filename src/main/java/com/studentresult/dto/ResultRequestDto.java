package com.studentresult.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Result creation and update requests
 * Contains validation annotations for data integrity
 * Marks are automatically calculated in the entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequestDto {
    
    @NotNull(message = "Student ID cannot be null")
    private Long studentId;
    
    @NotNull(message = "Subject ID cannot be null")
    private Long subjectId;
    
    @NotNull(message = "Internal marks cannot be null")
    @Min(value = 0, message = "Internal marks must be between 0 and 100")
    @Max(value = 100, message = "Internal marks must be between 0 and 100")
    private Integer internalMarks;
    
    @NotNull(message = "External marks cannot be null")
    @Min(value = 0, message = "External marks must be between 0 and 100")
    @Max(value = 100, message = "External marks must be between 0 and 100")
    private Integer externalMarks;
}
