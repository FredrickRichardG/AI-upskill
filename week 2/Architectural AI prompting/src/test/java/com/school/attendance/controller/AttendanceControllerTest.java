package com.school.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.attendance.dto.ApiResponse;
import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.model.Attendance;
import com.school.attendance.model.AttendanceStatus;
import com.school.attendance.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendanceService attendanceService;

    private UUID studentId;
    private UUID teacherId;
    private UUID classRoomId;
    private LocalDate date;
    private Attendance attendance;
    private AttendanceDTO attendanceDTO;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        teacherId = UUID.randomUUID();
        classRoomId = UUID.randomUUID();
        date = LocalDate.now();

        attendance = new Attendance();
        attendance.setId(UUID.randomUUID());
        attendance.setDate(date);
        attendance.setStatus(AttendanceStatus.PRESENT);

        attendanceDTO = new AttendanceDTO(studentId, date, AttendanceStatus.PRESENT, teacherId);
    }

    @Test
    void markAttendance_Success() throws Exception {
        when(attendanceService.markAttendance(any(), any(), any(), any()))
            .thenReturn(attendance);

        mockMvc.perform(post("/api/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attendanceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void markBulkAttendance_Success() throws Exception {
        List<AttendanceDTO> dtos = Arrays.asList(attendanceDTO);
        List<Attendance> attendances = Arrays.asList(attendance);

        when(attendanceService.markBulkAttendance(any()))
            .thenReturn(attendances);

        mockMvc.perform(post("/api/attendance/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getClassRoomAttendance_Success() throws Exception {
        Page<Attendance> attendancePage = new PageImpl<>(Arrays.asList(attendance));
        when(attendanceService.getClassRoomAttendance(any(), any(), any()))
            .thenReturn(attendancePage);

        mockMvc.perform(get("/api/attendance/class/{classRoomId}", classRoomId)
                .param("date", date.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getClassRoomAttendanceSummary_Success() throws Exception {
        Map<AttendanceStatus, Long> summary = Map.of(
            AttendanceStatus.PRESENT, 5L,
            AttendanceStatus.ABSENT, 2L,
            AttendanceStatus.LATE, 1L
        );

        when(attendanceService.getClassRoomAttendanceSummary(any(), any()))
            .thenReturn(summary);

        mockMvc.perform(get("/api/attendance/class/{classRoomId}/summary", classRoomId)
                .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.PRESENT").value(5))
                .andExpect(jsonPath("$.data.ABSENT").value(2))
                .andExpect(jsonPath("$.data.LATE").value(1));
    }

    @Test
    void getStudentAttendance_Success() throws Exception {
        Page<Attendance> attendancePage = new PageImpl<>(Arrays.asList(attendance));
        when(attendanceService.getStudentAttendance(any(), any(), any(), any()))
            .thenReturn(attendancePage);

        mockMvc.perform(get("/api/attendance/student/{studentId}", studentId)
                .param("startDate", date.minusDays(7).toString())
                .param("endDate", date.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getSchoolAttendanceSummary_Success() throws Exception {
        Map<AttendanceStatus, Long> summary = Map.of(
            AttendanceStatus.PRESENT, 50L,
            AttendanceStatus.ABSENT, 20L,
            AttendanceStatus.LATE, 10L
        );

        when(attendanceService.getSchoolAttendanceSummary(any()))
            .thenReturn(summary);

        mockMvc.perform(get("/api/attendance/school/summary")
                .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.PRESENT").value(50))
                .andExpect(jsonPath("$.data.ABSENT").value(20))
                .andExpect(jsonPath("$.data.LATE").value(10));
    }
} 