package com.studentresult.util;

/**
 * Application constants
 * Centralized location for constant values used throughout the application
 */
public class Constants {
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
    
    // Grade constants
    public static final String GRADE_A_PLUS = "A+";
    public static final String GRADE_A = "A";
    public static final String GRADE_B_PLUS = "B+";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_D = "D";
    public static final String GRADE_F = "F";
    
    // Result status constants
    public static final String STATUS_PASS = "PASS";
    public static final String STATUS_FAIL = "FAIL";
    
    // Grade thresholds
    public static final int THRESHOLD_A_PLUS = 90;
    public static final int THRESHOLD_A = 80;
    public static final int THRESHOLD_B_PLUS = 70;
    public static final int THRESHOLD_B = 60;
    public static final int THRESHOLD_C = 50;
    public static final int THRESHOLD_D = 35;
    
    // Marks validation
    public static final int MIN_MARKS = 0;
    public static final int MAX_MARKS = 100;
}
