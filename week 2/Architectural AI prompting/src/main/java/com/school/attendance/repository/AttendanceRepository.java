package com.school.attendance.repository;

import com.school.attendance.model.Attendance;
import com.school.attendance.model.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date = :date")
    Optional<Attendance> findByStudentAndDate(@Param("studentId") UUID studentId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.classRoom.id = :classRoomId AND a.date = :date")
    Page<Attendance> findByClassRoomAndDate(
        @Param("classRoomId") UUID classRoomId, 
        @Param("date") LocalDate date,
        Pageable pageable
    );

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :startDate AND :endDate")
    Page<Attendance> findByStudentAndDateRange(
        @Param("studentId") UUID studentId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.classRoom.id = :classRoomId AND a.date = :date GROUP BY a.status")
    List<Object[]> getClassRoomAttendanceSummary(@Param("classRoomId") UUID classRoomId, @Param("date") LocalDate date);

    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.date = :date GROUP BY a.status")
    List<Object[]> getSchoolAttendanceSummary(@Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.student.classRoom.id = :classRoomId AND a.date = :date")
    Page<Attendance> findByStudentClassRoomAndDate(UUID classRoomId, LocalDate date, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.classRoom.id = :classRoomId AND a.date = :date")
    List<Attendance> findByStudentClassRoomAndDate(UUID classRoomId, LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :startDate AND :endDate")
    Page<Attendance> findByStudentIdAndDateBetween(UUID studentId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStudentIdAndDate(UUID studentId, LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.classRoom.id = :classRoomId AND a.date = :date AND a.status = 'PRESENT'")
    long countPresentByClassRoomIdAndDate(@Param("classRoomId") UUID classRoomId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.classRoom.id = :classRoomId AND a.date = :date AND a.status = 'ABSENT'")
    long countAbsentByClassRoomIdAndDate(@Param("classRoomId") UUID classRoomId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.classRoom.id = :classRoomId AND a.date = :date AND a.status = 'LATE'")
    long countLateByClassRoomIdAndDate(@Param("classRoomId") UUID classRoomId, @Param("date") LocalDate date);
} 