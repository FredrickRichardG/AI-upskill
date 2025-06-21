package com.school.attendance.service;

import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.model.Attendance;
import com.school.attendance.model.AttendanceStatus;
import com.school.attendance.model.Student;
import com.school.attendance.exception.AttendanceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing student attendance.
 * Follows Single Responsibility Principle by focusing solely on attendance-related operations.
 */
public interface AttendanceService {
    /**
     * Mark attendance for a student on a specific date.
     *
     * @param studentId UUID of the student
     * @param date Date of attendance
     * @param status Attendance status (PRESENT, ABSENT, LATE)
     * @param markedById UUID of the teacher marking attendance
     * @return Created attendance record
     * @throws AttendanceException if attendance is already marked or invalid data
     */
    Attendance markAttendance(UUID studentId, LocalDate date, AttendanceStatus status, UUID markedById);

    /**
     * Mark attendance for multiple students.
     *
     * @param attendanceDTOs List of attendance records to mark
     * @return List of created attendance records
     * @throws AttendanceException if any attendance record is invalid
     */
    List<Attendance> markBulkAttendance(List<AttendanceDTO> attendanceDTOs);

    /**
     * Get attendance records for a specific class on a given date.
     *
     * @param classRoomId UUID of the class
     * @param date Date to fetch attendance for
     * @param pageable Pagination information
     * @return Page of attendance records
     */
    Page<Attendance> getClassRoomAttendance(UUID classRoomId, LocalDate date, Pageable pageable);

    /**
     * Get attendance summary for a class (present/absent counts).
     *
     * @param classRoomId UUID of the class
     * @param date Date to get summary for
     * @return Map containing attendance status counts
     */
    Map<AttendanceStatus, Long> getClassRoomAttendanceSummary(UUID classRoomId, LocalDate date);

    /**
     * Get attendance records for a specific student within a date range.
     *
     * @param studentId UUID of the student
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @param pageable Pagination information
     * @return Page of attendance records
     */
    Page<Attendance> getStudentAttendance(UUID studentId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get school-wide attendance summary for a specific date.
     *
     * @param date Date to get summary for
     * @return Map containing attendance status counts across all classes
     */
    Map<AttendanceStatus, Long> getSchoolAttendanceSummary(LocalDate date);
} 