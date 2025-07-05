package com.lms.courseservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String fileUrl;
    
    @Column(nullable = false)
    private String fileType; // PDF, VIDEO, DOCUMENT, IMAGE, etc.
    
    @Column(nullable = false)
    private Long fileSize; // in bytes
    
    @Column(nullable = false)
    private String mimeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    @Column(nullable = false)
    private Boolean isPublic = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private String downloadToken;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    @Builder
    public Resource(Long id, String name, String description, String fileUrl, String fileType, String mimeType, Long fileSize, Boolean isPublic, String downloadToken, LocalDateTime createdAt, LocalDateTime updatedAt, Course course, Module module, Lesson lesson) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.isPublic = isPublic;
        this.downloadToken = downloadToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.course = course;
        this.module = module;
        this.lesson = lesson;
    }
} 