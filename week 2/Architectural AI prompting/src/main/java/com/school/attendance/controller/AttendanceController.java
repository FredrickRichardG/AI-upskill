package com.school.attendance.controller;

import com.school.attendance.constant.AttendanceConstants;
import com.school.attendance.dto.ApiResponse;
import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.model.Attendance;
import com.school.attendance.model.AttendanceStatus;
import com.school.attendance.service.AttendanceService;
import com.school.attendance.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "APIs for managing student attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(
        summary = "Mark attendance for a student",
        description = "Marks attendance for a single student on a specific date"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attendance marked successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student or teacher not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Attendance>> markAttendance(
            @Valid @RequestBody AttendanceDTO attendanceDTO) {
        Attendance attendance = attendanceService.markAttendance(
            attendanceDTO.getStudentId(),
            attendanceDTO.getDate(),
            attendanceDTO.getStatus(),
            attendanceDTO.getMarkedById()
        );
        return ResponseEntity.ok(ApiResponse.success(attendance, "Attendance marked successfully"));
    }

    @Operation(
        summary = "Mark attendance for multiple students",
        description = "Marks attendance for multiple students in a single request"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk attendance marked successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student or teacher not found")
    })
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<Attendance>>> markBulkAttendance(
            @Valid @RequestBody List<AttendanceDTO> attendanceDTOs) {
        List<Attendance> attendances = attendanceService.markBulkAttendance(attendanceDTOs);
        return ResponseEntity.ok(ApiResponse.success(attendances, "Bulk attendance marked successfully"));
    }

    @Operation(
        summary = "Get attendance records for a class",
        description = "Retrieves paginated attendance records for a specific class on a given date"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Class attendance retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class not found")
    })
    @GetMapping("/class/{classRoomId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<Attendance>>> getClassRoomAttendance(
            @Parameter(description = "ID of the class room") @PathVariable UUID classRoomId,
            @Parameter(description = "Date to fetch attendance for") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<Attendance> attendancePage = attendanceService.getClassRoomAttendance(classRoomId, date, pageable);
        return ResponseEntity.ok(ApiResponse.paginated(attendancePage, "Class room attendance retrieved successfully"));
    }

    @Operation(
        summary = "Get attendance summary for a class",
        description = "Retrieves attendance summary (present/absent/late counts) for a specific class on a given date"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Class attendance summary retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Class not found")
    })
    @GetMapping("/class/{classRoomId}/summary")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<AttendanceStatus, Long>>> getClassRoomAttendanceSummary(
            @Parameter(description = "ID of the class room") @PathVariable UUID classRoomId,
            @Parameter(description = "Date to fetch summary for") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<AttendanceStatus, Long> summary = attendanceService.getClassRoomAttendanceSummary(classRoomId, date);
        return ResponseEntity.ok(ApiResponse.success(summary, "Class room attendance summary retrieved successfully"));
    }

    @Operation(
        summary = "Get attendance records for a student",
        description = "Retrieves paginated attendance records for a specific student within a date range"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student attendance retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<Attendance>>> getStudentAttendance(
            @Parameter(description = "ID of the student") @PathVariable UUID studentId,
            @Parameter(description = "Start date of the range") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date of the range") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<Attendance> attendancePage = attendanceService.getStudentAttendance(studentId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.paginated(attendancePage, "Student attendance retrieved successfully"));
    }

    @Operation(
        summary = "Get school-wide attendance summary",
        description = "Retrieves attendance summary (present/absent/late counts) for the entire school on a given date"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School attendance summary retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/school/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<AttendanceStatus, Long>>> getSchoolAttendanceSummary(
            @Parameter(description = "Date to fetch summary for") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<AttendanceStatus, Long> summary = attendanceService.getSchoolAttendanceSummary(date);
        return ResponseEntity.ok(ApiResponse.success(summary, "School attendance summary retrieved successfully"));
    }
} 