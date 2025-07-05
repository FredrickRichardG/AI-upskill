package com.lms.courseservice.service;

import com.lms.courseservice.dto.ContentVersionDto;
import com.lms.courseservice.model.ContentVersion;
import com.lms.courseservice.model.Course;
import com.lms.courseservice.model.VersionStatus;
import com.lms.courseservice.repository.ContentVersionRepository;
import com.lms.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContentVersionService {
    
    private final ContentVersionRepository versionRepository;
    private final CourseRepository courseRepository;
    
    // Content Version CRUD Operations
    public ContentVersionDto createVersion(Long courseId, ContentVersionDto request) {
        log.info("Creating new version for course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Generate version number
        String versionNumber = generateVersionNumber(courseId);
        
        ContentVersion version = ContentVersion.builder()
                .course(course)
                .version(versionNumber)
                .changeLog(request.getChangeLog())
                .status(VersionStatus.DRAFT)
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        ContentVersion savedVersion = versionRepository.save(version);
        return mapToDto(savedVersion);
    }
    
    public ContentVersionDto updateVersion(Long versionId, ContentVersionDto request) {
        log.info("Updating version: {}", versionId);
        
        ContentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        
        version.setChangeLog(request.getChangeLog());
        version.setComments(request.getComments());
        version.setUpdatedAt(LocalDateTime.now());
        
        ContentVersion savedVersion = versionRepository.save(version);
        return mapToDto(savedVersion);
    }
    
    public ContentVersionDto getVersionById(Long versionId) {
        ContentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        return mapToDto(version);
    }
    
    public List<ContentVersionDto> getVersionsByCourse(Long courseId) {
        List<ContentVersion> versions = versionRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        return versions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public ContentVersionDto getLatestVersionByCourse(Long courseId) {
        List<ContentVersion> versions = versionRepository.findLatestVersionsByCourse(courseId);
        if (versions.isEmpty()) {
            throw new RuntimeException("No versions found for course");
        }
        return mapToDto(versions.get(0));
    }
    
    public List<ContentVersionDto> getAllVersions() {
        List<ContentVersion> versions = versionRepository.findAllByOrderByCreatedAtDesc();
        return versions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public Page<ContentVersionDto> getAllVersions(Pageable pageable) {
        Page<ContentVersion> versions = versionRepository.findAll(pageable);
        return versions.map(this::mapToDto);
    }
    
    public void deleteVersion(Long versionId) {
        log.info("Deleting version: {}", versionId);
        versionRepository.deleteById(versionId);
    }
    
    // Version Status Management
    public ContentVersionDto submitForReview(Long versionId, String reviewer) {
        log.info("Submitting version for review: {}", versionId);
        
        ContentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        
        version.setStatus(VersionStatus.IN_REVIEW);
        version.setReviewedBy(reviewer);
        version.setUpdatedAt(LocalDateTime.now());
        
        ContentVersion savedVersion = versionRepository.save(version);
        return mapToDto(savedVersion);
    }
    
    public ContentVersionDto approveVersion(Long versionId, String reviewer, String comments) {
        log.info("Approving version: {}", versionId);
        
        ContentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        
        version.setStatus(VersionStatus.APPROVED);
        version.setReviewedBy(reviewer);
        version.setReviewedAt(LocalDateTime.now());
        version.setComments(comments);
        version.setUpdatedAt(LocalDateTime.now());
        
        ContentVersion savedVersion = versionRepository.save(version);
        return mapToDto(savedVersion);
    }
    
    public ContentVersionDto rejectVersion(Long versionId, String reviewer, String comments) {
        log.info("Rejecting version: {}", versionId);
        
        ContentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        
        version.setStatus(VersionStatus.REJECTED);
        version.setReviewedBy(reviewer);
        version.setReviewedAt(LocalDateTime.now());
        version.setComments(comments);
        version.setUpdatedAt(LocalDateTime.now());
        
        ContentVersion savedVersion = versionRepository.save(version);
        return mapToDto(savedVersion);
    }
    
    public ContentVersionDto publishVersion(Long versionId) {
        log.info("Publishing version: {}", versionId);
        
        ContentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        
        // Unpublish other versions of the same course
        List<ContentVersion> otherVersions = versionRepository.findByCourseIdAndStatus(
                version.getCourse().getId(), VersionStatus.PUBLISHED);
        for (ContentVersion otherVersion : otherVersions) {
            otherVersion.setStatus(VersionStatus.ARCHIVED);
            otherVersion.setUpdatedAt(LocalDateTime.now());
            versionRepository.save(otherVersion);
        }
        
        version.setStatus(VersionStatus.PUBLISHED);
        version.setUpdatedAt(LocalDateTime.now());
        
        ContentVersion savedVersion = versionRepository.save(version);
        return mapToDto(savedVersion);
    }
    
    // Version Search and Filtering
    public List<ContentVersionDto> getVersionsByStatus(VersionStatus status) {
        List<ContentVersion> versions = versionRepository.findByStatus(status);
        return versions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ContentVersionDto> getVersionsInReview() {
        List<ContentVersion> versions = versionRepository.findVersionsInReview();
        return versions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ContentVersionDto> getPublishedVersions() {
        List<ContentVersion> versions = versionRepository.findPublishedVersions();
        return versions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ContentVersionDto> searchVersionsByChangeLog(String keyword) {
        List<ContentVersion> versions = versionRepository.searchByChangeLog(keyword);
        return versions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Version Statistics
    public Long getVersionCount() {
        return versionRepository.count();
    }
    
    public Long getVersionCountByStatus(VersionStatus status) {
        return versionRepository.countByStatus(status);
    }
    
    // Helper Methods
    private String generateVersionNumber(Long courseId) {
        List<ContentVersion> existingVersions = versionRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        if (existingVersions.isEmpty()) {
            return "1.0.0";
        }
        
        // Simple version increment - in production, you might want more sophisticated versioning
        String lastVersion = existingVersions.get(0).getVersion();
        String[] parts = lastVersion.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        
        return String.format("%d.%d.%d", major, minor, patch + 1);
    }
    
    private ContentVersionDto mapToDto(ContentVersion version) {
        return ContentVersionDto.builder()
                .id(version.getId())
                .version(version.getVersion())
                .changeLog(version.getChangeLog())
                .status(version.getStatus())
                .createdBy(version.getCreatedBy())
                .reviewedBy(version.getReviewedBy())
                .reviewedAt(version.getReviewedAt())
                .comments(version.getComments())
                .createdAt(version.getCreatedAt())
                .updatedAt(version.getUpdatedAt())
                .courseId(version.getCourse().getId())
                .build();
    }
} 