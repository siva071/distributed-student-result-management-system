package com.studentresult.repository;

import com.studentresult.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Result entity
 * Extends JpaRepository for standard CRUD operations
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    
    /**
     * Find all results by student ID
     * @param studentId the student ID
     * @return List of results for the student
     */
    List<Result> findByStudentStudentId(Long studentId);
    
    /**
     * Find all results by subject ID
     * @param subjectId the subject ID
     * @return List of results for the subject
     */
    List<Result> findBySubjectSubjectId(Long subjectId);
    
    /**
     * Find result by student ID and subject ID
     * @param studentId the student ID
     * @param subjectId the subject ID
     * @return Optional containing the result if found
     */
    java.util.Optional<Result> findByStudentStudentIdAndSubjectSubjectId(Long studentId, Long subjectId);
}
