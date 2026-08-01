package com.studentresult.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a Student Result
 * Maps to the 'results' table in the database
 * Establishes Many-to-One relationship with Student and Subject
 */
@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;
    
    @NotNull(message = "Student cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @NotNull(message = "Subject cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @NotNull(message = "Internal marks cannot be null")
    @Min(value = 0, message = "Internal marks must be between 0 and 100")
    @Max(value = 100, message = "Internal marks must be between 0 and 100")
    @Column(name = "internal_marks", nullable = false)
    private Integer internalMarks;
    
    @NotNull(message = "External marks cannot be null")
    @Min(value = 0, message = "External marks must be between 0 and 100")
    @Max(value = 100, message = "External marks must be between 0 and 100")
    @Column(name = "external_marks", nullable = false)
    private Integer externalMarks;
    
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;
    
    @Column(name = "grade", length = 5)
    private String grade;
    
    @Column(name = "result_status", length = 10)
    private String resultStatus;
    
    /**
     * Automatically calculates total marks before persisting
     * totalMarks = internalMarks + externalMarks
     */
    @PrePersist
    @PreUpdate
    public void calculateTotalMarks() {
        if (internalMarks != null && externalMarks != null) {
            this.totalMarks = internalMarks + externalMarks;
            calculateGrade();
            calculateResultStatus();
        }
    }
    
    /**
     * Automatically assigns grade based on total marks
     * >=90 = A+, >=80 = A, >=70 = B+, >=60 = B, >=50 = C, >=35 = D, Below 35 = F
     */
    private void calculateGrade() {
        if (totalMarks >= 90) {
            this.grade = "A+";
        } else if (totalMarks >= 80) {
            this.grade = "A";
        } else if (totalMarks >= 70) {
            this.grade = "B+";
        } else if (totalMarks >= 60) {
            this.grade = "B";
        } else if (totalMarks >= 50) {
            this.grade = "C";
        } else if (totalMarks >= 35) {
            this.grade = "D";
        } else {
            this.grade = "F";
        }
    }
    
    /**
     * Automatically assigns result status based on total marks
     * >=35 = PASS, Below 35 = FAIL
     */
    private void calculateResultStatus() {
        this.resultStatus = totalMarks >= 35 ? "PASS" : "FAIL";
    }
}
