package com.school.attendance.repository;

import com.school.attendance.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    List<Student> findByClassRoomId(UUID classId);
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
} 