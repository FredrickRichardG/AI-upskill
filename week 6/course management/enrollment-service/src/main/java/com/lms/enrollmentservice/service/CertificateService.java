package com.lms.enrollmentservice.service;

import com.lms.enrollmentservice.model.Certificate;
import com.lms.enrollmentservice.model.CertificateStatus;
import com.lms.enrollmentservice.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {
    
    private final CertificateRepository certificateRepository;
    private final ProgressTrackingService progressTrackingService;
    private final NotificationService notificationService;
    
    /**
     * Generate certificate for course completion
     */
    @Transactional
    @Async
    public CompletableFuture<Certificate> generateCertificate(Long userId, Long enrollmentId, Long courseId, 
                                                           String courseTitle, String studentName, 
                                                           Double finalScore, String instructorName) {
        log.info("Generating certificate for user {} course {}", userId, courseId);
        
        // Check if certificate already exists
        Optional<Certificate> existingCert = certificateRepository.findByEnrollmentId(enrollmentId);
        if (existingCert.isPresent()) {
            log.info("Certificate already exists for enrollment {}", enrollmentId);
            return CompletableFuture.completedFuture(existingCert.get());
        }
        
        // Generate certificate number
        String certificateNumber = generateCertificateNumber();
        
        // Determine grade based on score
        String grade = determineGrade(finalScore);
        
        // Generate certificate hash for verification
        String certificateHash = generateCertificateHash(userId, courseId, certificateNumber);
        
        Certificate certificate = Certificate.builder()
                .certificateNumber(certificateNumber)
                .userId(userId)
                .enrollmentId(enrollmentId)
                .courseId(courseId)
                .courseTitle(courseTitle)
                .studentName(studentName)
                .finalScore(finalScore)
                .grade(grade)
                .instructorName(instructorName)
                .certificateHash(certificateHash)
                .status(CertificateStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusYears(2)) // Certificates valid for 2 years
                .build();
        
        Certificate savedCertificate = certificateRepository.save(certificate);
        
        // Async notification for certificate generation
        notifyCertificateGenerated(savedCertificate);
        
        return CompletableFuture.completedFuture(savedCertificate);
    }
    
    /**
     * Get certificate by ID
     */
    public Optional<Certificate> getCertificateById(Long certificateId) {
        return certificateRepository.findById(certificateId);
    }
    
    /**
     * Get certificate by certificate number
     */
    public Optional<Certificate> getCertificateByNumber(String certificateNumber) {
        return certificateRepository.findByCertificateNumber(certificateNumber);
    }
    
    /**
     * Get user certificates
     */
    public List<Certificate> getUserCertificates(Long userId) {
        return certificateRepository.findByUserId(userId);
    }
    
    /**
     * Get course certificates
     */
    public List<Certificate> getCourseCertificates(Long courseId) {
        return certificateRepository.findByCourseId(courseId);
    }
    
    /**
     * Verify certificate authenticity
     */
    public boolean verifyCertificate(String certificateNumber, String certificateHash) {
        Optional<Certificate> certificate = certificateRepository.findByCertificateNumber(certificateNumber);
        
        if (certificate.isEmpty()) {
            return false;
        }
        
        Certificate cert = certificate.get();
        String expectedHash = generateCertificateHash(cert.getUserId(), cert.getCourseId(), certificateNumber);
        
        return expectedHash.equals(certificateHash) && 
               CertificateStatus.ACTIVE.equals(cert.getStatus()) &&
               (cert.getExpiresAt() == null || cert.getExpiresAt().isAfter(LocalDateTime.now()));
    }
    
    /**
     * Revoke certificate
     */
    @Transactional
    public Certificate revokeCertificate(Long certificateId, String reason) {
        log.info("Revoking certificate {}", certificateId);
        
        Optional<Certificate> certificateOpt = certificateRepository.findById(certificateId);
        if (certificateOpt.isEmpty()) {
            throw new RuntimeException("Certificate not found");
        }
        
        Certificate certificate = certificateOpt.get();
        certificate.setStatus(CertificateStatus.REVOKED);
        
        Certificate savedCertificate = certificateRepository.save(certificate);
        
        // Async notification for certificate revocation
        notifyCertificateRevoked(savedCertificate, reason);
        
        return savedCertificate;
    }
    
    /**
     * Get certificate analytics
     */
    public Map<String, Object> getCertificateAnalytics(Long courseId) {
        List<Certificate> certificates = certificateRepository.findByCourseId(courseId);
        
        int totalCertificates = certificates.size();
        int activeCertificates = (int) certificates.stream()
                .filter(cert -> CertificateStatus.ACTIVE.equals(cert.getStatus()))
                .count();
        
        double averageScore = certificates.stream()
                .mapToDouble(Certificate::getFinalScore)
                .average()
                .orElse(0.0);
        
        return Map.of(
                "courseId", courseId,
                "totalCertificates", totalCertificates,
                "activeCertificates", activeCertificates,
                "expiredCertificates", totalCertificates - activeCertificates,
                "averageScore", averageScore
        );
    }
    
    /**
     * Generate certificate number
     */
    private String generateCertificateNumber() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Generate certificate hash for verification
     */
    private String generateCertificateHash(Long userId, Long courseId, String certificateNumber) {
        String data = userId + ":" + courseId + ":" + certificateNumber + ":" + "LMS_SECRET_KEY";
        return UUID.nameUUIDFromBytes(data.getBytes()).toString();
    }
    
    /**
     * Determine grade based on score
     */
    private String determineGrade(Double score) {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }
    
    /**
     * Async notification for certificate generation
     */
    @Async
    public CompletableFuture<Void> notifyCertificateGenerated(Certificate certificate) {
        log.info("Sending certificate generation notification for user {} certificate {}", 
                certificate.getUserId(), certificate.getCertificateNumber());
        
        // TODO: Implement notification logic
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Async notification for certificate revocation
     */
    @Async
    public CompletableFuture<Void> notifyCertificateRevoked(Certificate certificate, String reason) {
        log.info("Sending certificate revocation notification for user {} certificate {} reason {}", 
                certificate.getUserId(), certificate.getCertificateNumber(), reason);
        
        // TODO: Implement notification logic
        return CompletableFuture.completedFuture(null);
    }
} 