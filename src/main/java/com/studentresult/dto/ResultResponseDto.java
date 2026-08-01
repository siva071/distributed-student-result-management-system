package com.studentresult.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Result response
 * Includes calculated fields: totalMarks, grade, and resultStatus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponseDto {
    
    private Long resultId;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Integer internalMarks;
    private Integer externalMarks;
    private Integer totalMarks;
    private String grade;
    private String resultStatus;
}
