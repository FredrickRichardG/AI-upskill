package com.lms.courseservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    
    @Column(nullable = false, unique = true)
    private String slug;
    
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;
    
    @Column(nullable = false)
    private String thumbnailUrl;
    
    private String previewVideoUrl;
    
    private Integer duration; // in minutes
    
    @Column(nullable = false)
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = false;
    
    private Integer maxStudents;
    
    @Column(columnDefinition = "TEXT")
    private String prerequisites;
    
    @Column(columnDefinition = "TEXT")
    private String learningObjectives;
    
    @Column(columnDefinition = "TEXT")
    private String targetAudience;
    
    @Column(columnDefinition = "TEXT")
    private String certificateTemplate;
    
    private LocalDateTime publishedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToMany
    @JoinTable(
        name = "course_tags",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseAssignment> assignments = new ArrayList<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentVersion> versions = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 