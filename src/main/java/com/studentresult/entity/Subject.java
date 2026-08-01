package com.studentresult.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing a Subject
 * Maps to the 'subjects' table in the database
 */
@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;
    
    @NotBlank(message = "Subject code cannot be null")
    @Column(name = "subject_code", unique = true, nullable = false, length = 20)
    private String subjectCode;
    
    @NotBlank(message = "Subject name cannot be null")
    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;
    
    @NotBlank(message = "Department cannot be null")
    @Column(name = "department", nullable = false, length = 50)
    private String department;
    
    @NotNull(message = "Semester cannot be null")
    @Column(name = "semester", nullable = false)
    private Integer semester;
    
    @NotNull(message = "Credits cannot be null")
    @Positive(message = "Credits must be greater than zero")
    @Column(name = "credits", nullable = false)
    private Integer credits;
    
    // One-to-many relationship with Result
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results;
}
