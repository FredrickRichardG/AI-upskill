package com.school.attendance.service.impl;

import com.school.attendance.constant.AttendanceConstants;
import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.exception.ResourceNotFoundException;
import com.school.attendance.model.Attendance;
import com.school.attendance.model.AttendanceStatus;
import com.school.attendance.model.Student;
import com.school.attendance.model.Teacher;
import com.school.attendance.repository.AttendanceRepository;
import com.school.attendance.repository.StudentRepository;
import com.school.attendance.repository.TeacherRepository;
import com.school.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    @CacheEvict(value = {AttendanceConstants.CACHE_STUDENT_ATTENDANCE, AttendanceConstants.CACHE_CLASS_ATTENDANCE}, allEntries = true)
    public Attendance markAttendance(UUID studentId, LocalDate date, AttendanceStatus status, UUID markedById) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(AttendanceConstants.ERROR_STUDENT_NOT_FOUND));
        
        Teacher teacher = teacherRepository.findById(markedById)
                .orElseThrow(() -> new ResourceNotFoundException(AttendanceConstants.ERROR_TEACHER_NOT_FOUND));

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setClassRoom(student.getClassRoom());
        attendance.setDate(date);
        attendance.setStatus(status);
        attendance.setMarkedBy(teacher);

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    @CacheEvict(value = {AttendanceConstants.CACHE_STUDENT_ATTENDANCE, AttendanceConstants.CACHE_CLASS_ATTENDANCE}, allEntries = true)
    public List<Attendance> markBulkAttendance(List<AttendanceDTO> attendanceDTOs) {
        return attendanceDTOs.stream()
                .map(dto -> markAttendance(
                    dto.getStudentId(),
                    dto.getDate(),
                    dto.getStatus(),
                    dto.getMarkedById()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = AttendanceConstants.CACHE_CLASS_ATTENDANCE, key = "#classRoomId + '-' + #date + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Attendance> getClassRoomAttendance(UUID classRoomId, LocalDate date, Pageable pageable) {
        return attendanceRepository.findByClassRoomAndDate(classRoomId, date, pageable);
    }

    @Override
    @Cacheable(value = AttendanceConstants.CACHE_ATTENDANCE_SUMMARY, key = "#classRoomId + '-' + #date")
    public Map<AttendanceStatus, Long> getClassRoomAttendanceSummary(UUID classRoomId, LocalDate date) {
        List<Object[]> summary = attendanceRepository.getClassRoomAttendanceSummary(classRoomId, date);
        return summary.stream()
                .collect(Collectors.toMap(
                    row -> AttendanceStatus.valueOf((String) row[0]),
                    row -> (Long) row[1]
                ));
    }

    @Override
    @Cacheable(value = AttendanceConstants.CACHE_STUDENT_ATTENDANCE, key = "#studentId + '-' + #startDate + '-' + #endDate + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Attendance> getStudentAttendance(UUID studentId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return attendanceRepository.findByStudentAndDateRange(studentId, startDate, endDate, pageable);
    }

    @Override
    @Cacheable(value = AttendanceConstants.CACHE_SCHOOL_ATTENDANCE, key = "#date")
    public Map<AttendanceStatus, Long> getSchoolAttendanceSummary(LocalDate date) {
        List<Object[]> summary = attendanceRepository.getSchoolAttendanceSummary(date);
        return summary.stream()
                .collect(Collectors.toMap(
                    row -> AttendanceStatus.valueOf((String) row[0]),
                    row -> (Long) row[1]
                ));
    }
} 