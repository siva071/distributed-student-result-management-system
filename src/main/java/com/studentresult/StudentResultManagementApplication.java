package com.studentresult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for Distributed Student Result Management System
 * This is the entry point for the Spring Boot application
 */
@SpringBootApplication
@EnableCaching
public class StudentResultManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StudentResultManagementApplication.class, args);
        System.out.println("========================================");
        System.out.println("Student Result Management System Started!");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("API Docs: http://localhost:8080/api-docs");
        System.out.println("========================================");
    }
}
