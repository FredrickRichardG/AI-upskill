package com.school.attendance.service;

import com.school.attendance.constant.AttendanceConstants;
import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.exception.ResourceNotFoundException;
import com.school.attendance.model.*;
import com.school.attendance.repository.AttendanceRepository;
import com.school.attendance.repository.StudentRepository;
import com.school.attendance.repository.TeacherRepository;
import com.school.attendance.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Student student;
    private Teacher teacher;
    private ClassRoom classRoom;
    private Attendance attendance;
    private UUID studentId;
    private UUID teacherId;
    private UUID classRoomId;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        teacherId = UUID.randomUUID();
        classRoomId = UUID.randomUUID();
        date = LocalDate.now();

        classRoom = new ClassRoom();
        classRoom.setId(classRoomId);
        classRoom.setName("Class 10A");
        classRoom.setGrade("10");
        classRoom.setSection("A");

        student = new Student();
        student.setId(studentId);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setClassRoom(classRoom);

        teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setClassRoom(classRoom);

        attendance = new Attendance();
        attendance.setId(UUID.randomUUID());
        attendance.setStudent(student);
        attendance.setClassRoom(classRoom);
        attendance.setDate(date);
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setMarkedBy(teacher);
    }

    @Test
    void markAttendance_Success() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        Attendance result = attendanceService.markAttendance(studentId, date, AttendanceStatus.PRESENT, teacherId);

        assertNotNull(result);
        assertEquals(AttendanceStatus.PRESENT, result.getStatus());
        assertEquals(student, result.getStudent());
        assertEquals(teacher, result.getMarkedBy());
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void markAttendance_StudentNotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            attendanceService.markAttendance(studentId, date, AttendanceStatus.PRESENT, teacherId)
        );
    }

    @Test
    void markAttendance_TeacherNotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            attendanceService.markAttendance(studentId, date, AttendanceStatus.PRESENT, teacherId)
        );
    }

    @Test
    void markBulkAttendance_Success() {
        List<AttendanceDTO> dtos = Arrays.asList(
            new AttendanceDTO(studentId, date, AttendanceStatus.PRESENT, teacherId),
            new AttendanceDTO(studentId, date, AttendanceStatus.ABSENT, teacherId)
        );

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        List<Attendance> results = attendanceService.markBulkAttendance(dtos);

        assertNotNull(results);
        assertEquals(2, results.size());
        verify(attendanceRepository, times(2)).save(any(Attendance.class));
    }

    @Test
    void getClassRoomAttendance_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Attendance> attendances = Arrays.asList(attendance);
        Page<Attendance> attendancePage = new PageImpl<>(attendances);

        when(attendanceRepository.findByClassRoomAndDate(classRoomId, date, pageable))
            .thenReturn(attendancePage);

        Page<Attendance> result = attendanceService.getClassRoomAttendance(classRoomId, date, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(attendanceRepository).findByClassRoomAndDate(classRoomId, date, pageable);
    }

    @Test
    void getClassRoomAttendanceSummary_Success() {
        List<Object[]> summary = Arrays.asList(
            new Object[]{AttendanceStatus.PRESENT.name(), 5L},
            new Object[]{AttendanceStatus.ABSENT.name(), 2L},
            new Object[]{AttendanceStatus.LATE.name(), 1L}
        );

        when(attendanceRepository.getClassRoomAttendanceSummary(classRoomId, date))
            .thenReturn(summary);

        Map<AttendanceStatus, Long> result = attendanceService.getClassRoomAttendanceSummary(classRoomId, date);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(5L, result.get(AttendanceStatus.PRESENT));
        assertEquals(2L, result.get(AttendanceStatus.ABSENT));
        assertEquals(1L, result.get(AttendanceStatus.LATE));
    }

    @Test
    void getStudentAttendance_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate startDate = date.minusDays(7);
        LocalDate endDate = date;
        List<Attendance> attendances = Arrays.asList(attendance);
        Page<Attendance> attendancePage = new PageImpl<>(attendances);

        when(attendanceRepository.findByStudentAndDateRange(studentId, startDate, endDate, pageable))
            .thenReturn(attendancePage);

        Page<Attendance> result = attendanceService.getStudentAttendance(studentId, startDate, endDate, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(attendanceRepository).findByStudentAndDateRange(studentId, startDate, endDate, pageable);
    }

    @Test
    void getSchoolAttendanceSummary_Success() {
        List<Object[]> summary = Arrays.asList(
            new Object[]{AttendanceStatus.PRESENT.name(), 50L},
            new Object[]{AttendanceStatus.ABSENT.name(), 20L},
            new Object[]{AttendanceStatus.LATE.name(), 10L}
        );

        when(attendanceRepository.getSchoolAttendanceSummary(date))
            .thenReturn(summary);

        Map<AttendanceStatus, Long> result = attendanceService.getSchoolAttendanceSummary(date);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(50L, result.get(AttendanceStatus.PRESENT));
        assertEquals(20L, result.get(AttendanceStatus.ABSENT));
        assertEquals(10L, result.get(AttendanceStatus.LATE));
    }
} 