package com.studentresult.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing a Student
 * Maps to the 'students' table in the database
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;
    
    @NotBlank(message = "Hall ticket number cannot be null")
    @Column(name = "hall_ticket_no", unique = true, nullable = false, length = 50)
    private String hallTicketNo;
    
    @NotBlank(message = "Full name cannot be null")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @NotBlank(message = "Gender cannot be null")
    @Column(name = "gender", nullable = false, length = 10)
    private String gender;
    
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @NotBlank(message = "Phone cannot be null")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    @NotBlank(message = "Department cannot be null")
    @Column(name = "department", nullable = false, length = 50)
    private String department;
    
    @NotNull(message = "Year of study cannot be null")
    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy;
    
    @NotNull(message = "Semester cannot be null")
    @Column(name = "semester", nullable = false)
    private Integer semester;
    
    @NotBlank(message = "Section cannot be null")
    @Column(name = "section", nullable = false, length = 10)
    private String section;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // One-to-many relationship with Result
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results;
}
