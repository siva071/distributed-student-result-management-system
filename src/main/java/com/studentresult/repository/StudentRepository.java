package com.studentresult.repository;

import com.studentresult.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Student entity
 * Extends JpaRepository for standard CRUD operations
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Find student by hall ticket number
     * @param hallTicketNo the hall ticket number
     * @return Optional containing the student if found
     */
    Optional<Student> findByHallTicketNo(String hallTicketNo);
    
    /**
     * Check if student exists by hall ticket number
     * @param hallTicketNo the hall ticket number
     * @return true if exists, false otherwise
     */
    boolean existsByHallTicketNo(String hallTicketNo);
    
    /**
     * Check if student exists by email
     * @param email the email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
