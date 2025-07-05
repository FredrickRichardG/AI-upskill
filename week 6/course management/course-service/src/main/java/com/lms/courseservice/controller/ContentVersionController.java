package com.lms.courseservice.controller;

import com.lms.courseservice.dto.ContentVersionDto;
import com.lms.courseservice.service.ContentVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content-versions")
@RequiredArgsConstructor
@Slf4j
public class ContentVersionController {
    
    private final ContentVersionService versionService;
    
    // Content Version CRUD Operations
    @PostMapping("/courses/{courseId}")
    public ResponseEntity<ContentVersionDto> createVersion(@PathVariable Long courseId, 
                                                        @RequestBody ContentVersionDto request) {
        log.info("Creating version for course: {}", courseId);
        ContentVersionDto version = versionService.createVersion(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(version);
    }
    
    @PutMapping("/{versionId}")
    public ResponseEntity<ContentVersionDto> updateVersion(@PathVariable Long versionId, 
                                                        @RequestBody ContentVersionDto request) {
        log.info("Updating version: {}", versionId);
        ContentVersionDto version = versionService.updateVersion(versionId, request);
        return ResponseEntity.ok(version);
    }
    
    @GetMapping("/{versionId}")
    public ResponseEntity<ContentVersionDto> getVersionById(@PathVariable Long versionId) {
        log.info("Getting version by ID: {}", versionId);
        ContentVersionDto version = versionService.getVersionById(versionId);
        return ResponseEntity.ok(version);
    }
    
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<ContentVersionDto>> getVersionsByCourse(@PathVariable Long courseId) {
        log.info("Getting versions for course: {}", courseId);
        List<ContentVersionDto> versions = versionService.getVersionsByCourse(courseId);
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/courses/{courseId}/latest")
    public ResponseEntity<ContentVersionDto> getLatestVersionByCourse(@PathVariable Long courseId) {
        log.info("Getting latest version for course: {}", courseId);
        ContentVersionDto version = versionService.getLatestVersionByCourse(courseId);
        return ResponseEntity.ok(version);
    }
    
    @GetMapping
    public ResponseEntity<List<ContentVersionDto>> getAllVersions() {
        log.info("Getting all versions");
        List<ContentVersionDto> versions = versionService.getAllVersions();
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/paged")
    public ResponseEntity<Page<ContentVersionDto>> getAllVersionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting all versions - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentVersionDto> versions = versionService.getAllVersions(pageable);
        return ResponseEntity.ok(versions);
    }
    
    @DeleteMapping("/{versionId}")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long versionId) {
        log.info("Deleting version: {}", versionId);
        versionService.deleteVersion(versionId);
        return ResponseEntity.noContent().build();
    }
    
    // Version Status Management
    @PostMapping("/{versionId}/submit")
    public ResponseEntity<ContentVersionDto> submitForReview(@PathVariable Long versionId,
                                                           @RequestParam String reviewer) {
        log.info("Submitting version for review: {}", versionId);
        ContentVersionDto version = versionService.submitForReview(versionId, reviewer);
        return ResponseEntity.ok(version);
    }
    
    @PostMapping("/{versionId}/approve")
    public ResponseEntity<ContentVersionDto> approveVersion(@PathVariable Long versionId,
                                                         @RequestParam String reviewer,
                                                         @RequestParam(required = false) String comments) {
        log.info("Approving version: {}", versionId);
        ContentVersionDto version = versionService.approveVersion(versionId, reviewer, comments);
        return ResponseEntity.ok(version);
    }
    
    @PostMapping("/{versionId}/reject")
    public ResponseEntity<ContentVersionDto> rejectVersion(@PathVariable Long versionId,
                                                        @RequestParam String reviewer,
                                                        @RequestParam(required = false) String comments) {
        log.info("Rejecting version: {}", versionId);
        ContentVersionDto version = versionService.rejectVersion(versionId, reviewer, comments);
        return ResponseEntity.ok(version);
    }
    
    @PostMapping("/{versionId}/publish")
    public ResponseEntity<ContentVersionDto> publishVersion(@PathVariable Long versionId) {
        log.info("Publishing version: {}", versionId);
        ContentVersionDto version = versionService.publishVersion(versionId);
        return ResponseEntity.ok(version);
    }
    
    // Version Search and Filtering
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContentVersionDto>> getVersionsByStatus(@PathVariable String status) {
        log.info("Getting versions by status: {}", status);
        List<ContentVersionDto> versions = versionService.getVersionsByStatus(
                com.lms.courseservice.model.VersionStatus.valueOf(status));
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/in-review")
    public ResponseEntity<List<ContentVersionDto>> getVersionsInReview() {
        log.info("Getting versions in review");
        List<ContentVersionDto> versions = versionService.getVersionsInReview();
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<ContentVersionDto>> getPublishedVersions() {
        log.info("Getting published versions");
        List<ContentVersionDto> versions = versionService.getPublishedVersions();
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ContentVersionDto>> searchVersionsByChangeLog(@RequestParam String keyword) {
        log.info("Searching versions by change log: {}", keyword);
        List<ContentVersionDto> versions = versionService.searchVersionsByChangeLog(keyword);
        return ResponseEntity.ok(versions);
    }
    
    // Version Statistics
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getVersionCount() {
        log.info("Getting version count");
        Long count = versionService.getVersionCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/count/{status}")
    public ResponseEntity<Long> getVersionCountByStatus(@PathVariable String status) {
        log.info("Getting version count by status: {}", status);
        Long count = versionService.getVersionCountByStatus(
                com.lms.courseservice.model.VersionStatus.valueOf(status));
        return ResponseEntity.ok(count);
    }
    
    // Health Check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Content Version Service is running");
    }
} 