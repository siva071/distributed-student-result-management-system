package com.studentresult.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Student creation and update requests
 * Contains validation annotations for data integrity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDto {
    
    @NotBlank(message = "Hall ticket number cannot be null")
    private String hallTicketNo;
    
    @NotBlank(message = "Full name cannot be null")
    private String fullName;
    
    @NotBlank(message = "Gender cannot be null")
    private String gender;
    
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Phone cannot be null")
    private String phone;
    
    @NotBlank(message = "Department cannot be null")
    private String department;
    
    @NotNull(message = "Year of study cannot be null")
    private Integer yearOfStudy;
    
    @NotNull(message = "Semester cannot be null")
    private Integer semester;
    
    @NotBlank(message = "Section cannot be null")
    private String section;
}
