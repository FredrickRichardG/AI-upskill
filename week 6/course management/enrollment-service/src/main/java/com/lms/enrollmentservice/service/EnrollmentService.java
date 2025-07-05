package com.lms.enrollmentservice.service;

import com.lms.enrollmentservice.model.Enrollment;
import com.lms.enrollmentservice.model.EnrollmentStatus;
import com.lms.enrollmentservice.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    
    /**
     * Create a new enrollment
     */
    @Transactional
    public Enrollment createEnrollment(Long userId, Long courseId) {
        log.info("Creating enrollment for user {} in course {}", userId, courseId);
        
        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .courseId(courseId)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .build();
        
        return enrollmentRepository.save(enrollment);
    }
    
    /**
     * Get enrollment by ID
     */
    public Optional<Enrollment> getEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }
    
    /**
     * Get enrollment by user and course
     */
    public Optional<Enrollment> getEnrollmentByUserAndCourse(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
    }
    
    /**
     * Get user enrollments
     */
    public List<Enrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }
    
    /**
     * Get course enrollments
     */
    public List<Enrollment> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
    
    /**
     * Update enrollment status
     */
    @Transactional
    public Enrollment updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
        log.info("Updating enrollment {} status to {}", enrollmentId, status);
        
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setStatus(status);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        
        if (status == EnrollmentStatus.COMPLETED) {
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        
        return enrollmentRepository.save(enrollment);
    }
    
    /**
     * Update enrollment progress
     */
    @Transactional
    public Enrollment updateEnrollmentProgress(Long enrollmentId, Integer completedLessons, 
                                            Integer totalLessons, Double overallProgress) {
        log.info("Updating enrollment {} progress: {}/{} lessons, {}%", 
                enrollmentId, completedLessons, totalLessons, overallProgress);
        
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setCompletedLessons(completedLessons);
        enrollment.setTotalLessons(totalLessons);
        enrollment.setOverallProgress(overallProgress);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        
        return enrollmentRepository.save(enrollment);
    }
} 