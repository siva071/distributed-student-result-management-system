package com.studentresult.repository;

import com.studentresult.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Subject entity
 * Extends JpaRepository for standard CRUD operations
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    /**
     * Find subject by subject code
     * @param subjectCode the subject code
     * @return Optional containing the subject if found
     */
    Optional<Subject> findBySubjectCode(String subjectCode);
    
    /**
     * Check if subject exists by subject code
     * @param subjectCode the subject code
     * @return true if exists, false otherwise
     */
    boolean existsBySubjectCode(String subjectCode);
}
