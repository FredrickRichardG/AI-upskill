package com.school.attendance.constant;

/**
 * Constants used throughout the attendance management system.
 */
public final class AttendanceConstants {
    private AttendanceConstants() {
        throw new IllegalStateException("Constants class");
    }

    // Cache names
    public static final String CACHE_CLASS_ATTENDANCE = "classAttendance";
    public static final String CACHE_STUDENT_ATTENDANCE = "studentAttendance";
    public static final String CACHE_SCHOOL_ATTENDANCE = "schoolAttendance";
    public static final String CACHE_ATTENDANCE_SUMMARY = "attendanceSummary";

    // Error codes
    public static final String ERROR_STUDENT_NOT_FOUND = "Student not found";
    public static final String ERROR_TEACHER_NOT_FOUND = "Teacher not found";
    public static final String ERROR_CLASS_NOT_FOUND = "Class not found";
    public static final String ERROR_ATTENDANCE_ALREADY_MARKED = "ATT-003";
    public static final String ERROR_INVALID_DATE = "ATT-004";
    public static final String ERROR_BULK_OPERATION_FAILED = "ATT-005";

    // Cache key patterns
    public static final String CACHE_KEY_CLASS_ATTENDANCE = "%s-%s-%d-%d";
    public static final String CACHE_KEY_STUDENT_ATTENDANCE = "%s-%s-%s-%d-%d";
    public static final String CACHE_KEY_CLASS_SUMMARY = "class-%s-%s";
    public static final String CACHE_KEY_SCHOOL_SUMMARY = "school-%s";
    public static final String CACHE_KEY_SCHOOL_ATTENDANCE = "school-%s";

    // Validation messages
    public static final String MSG_STUDENT_ID_REQUIRED = "Student ID is required";
    public static final String MSG_DATE_REQUIRED = "Date is required";
    public static final String MSG_STATUS_REQUIRED = "Status is required";
    public static final String MSG_MARKED_BY_REQUIRED = "Marked by ID is required";
} 