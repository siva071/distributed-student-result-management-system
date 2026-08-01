package com.studentresult.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Subject response
 * Provides clean API response for subject data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDto {
    
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private String department;
    private Integer semester;
    private Integer credits;
}
