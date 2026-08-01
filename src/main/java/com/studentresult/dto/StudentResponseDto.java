package com.studentresult.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Student response
 * Excludes sensitive information and provides clean API response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    
    private Long studentId;
    private String hallTicketNo;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String email;
    private String phone;
    private String department;
    private Integer yearOfStudy;
    private Integer semester;
    private String section;
    private LocalDateTime createdAt;
}
