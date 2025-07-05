package com.lms.enrollmentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String certificateNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long enrollmentId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private String courseTitle;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;

    // Certificate details
    private Double finalScore;
    private String grade;
    private String instructorName;
    private String certificateUrl; // URL to generated PDF
    private String certificateHash; // For verification

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status;

    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
        if (status == null) {
            status = CertificateStatus.ACTIVE;
        }
    }
} 