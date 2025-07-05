package com.lms.enrollmentservice.repository;

import com.lms.enrollmentservice.model.Certificate;
import com.lms.enrollmentservice.model.CertificateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    // Find by user
    List<Certificate> findByUserId(Long userId);
    
    // Find by course
    List<Certificate> findByCourseId(Long courseId);
    
    // Find by enrollment
    Optional<Certificate> findByEnrollmentId(Long enrollmentId);
    
    // Find by certificate number
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    
    // Find by status
    List<Certificate> findByStatus(CertificateStatus status);
    List<Certificate> findByUserIdAndStatus(Long userId, CertificateStatus status);
    
    // Find active certificates
    List<Certificate> findByStatusAndExpiresAtAfter(CertificateStatus status, LocalDateTime date);
    
    // Find expired certificates
    @Query("SELECT c FROM Certificate c WHERE c.status = 'ACTIVE' AND c.expiresAt < :date")
    List<Certificate> findExpiredCertificates(@Param("date") LocalDateTime date);
    
    // Find certificates by date range
    @Query("SELECT c FROM Certificate c WHERE c.issuedAt BETWEEN :startDate AND :endDate")
    List<Certificate> findByIssuedDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    // Find certificates by score range
    @Query("SELECT c FROM Certificate c WHERE c.finalScore BETWEEN :minScore AND :maxScore")
    List<Certificate> findByScoreRange(@Param("minScore") Double minScore, 
                                     @Param("maxScore") Double maxScore);
    
    // Find certificates by grade
    List<Certificate> findByGrade(String grade);
    
    // Analytics queries
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.userId = :userId")
    long countByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.courseId = :courseId")
    long countByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(c.finalScore) FROM Certificate c WHERE c.userId = :userId")
    Double getAverageScoreByUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(c.finalScore) FROM Certificate c WHERE c.courseId = :courseId")
    Double getAverageScoreByCourse(@Param("courseId") Long courseId);
    
    // Find recent certificates
    List<Certificate> findByUserIdOrderByIssuedAtDesc(Long userId);
    List<Certificate> findByCourseIdOrderByIssuedAtDesc(Long courseId);
    
    // Pagination
    Page<Certificate> findByUserId(Long userId, Pageable pageable);
    Page<Certificate> findByCourseId(Long courseId, Pageable pageable);
    
    // Find by instructor
    List<Certificate> findByInstructorName(String instructorName);
    
    // Find certificates that need renewal
    @Query("SELECT c FROM Certificate c WHERE c.status = 'ACTIVE' AND c.expiresAt BETWEEN :startDate AND :endDate")
    List<Certificate> findCertificatesNeedingRenewal(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
} 