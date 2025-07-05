package com.lms.courseservice.controller;

import com.lms.courseservice.dto.ResourceDto;
import com.lms.courseservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {
    
    private final ResourceService resourceService;
    
    // Resource CRUD Operations
    @PostMapping
    public ResponseEntity<ResourceDto> createResource(@RequestBody ResourceDto request) {
        log.info("Creating resource: {}", request.getName());
        ResourceDto resource = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
    
    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceDto> updateResource(@PathVariable Long resourceId, 
                                                   @RequestBody ResourceDto request) {
        log.info("Updating resource: {}", resourceId);
        ResourceDto resource = resourceService.updateResource(resourceId, request);
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceDto> getResourceById(@PathVariable Long resourceId) {
        log.info("Getting resource by ID: {}", resourceId);
        ResourceDto resource = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<ResourceDto>> getResourcesByCourse(@PathVariable Long courseId) {
        log.info("Getting resources for course: {}", courseId);
        List<ResourceDto> resources = resourceService.getResourcesByCourse(courseId);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<List<ResourceDto>> getResourcesByModule(@PathVariable Long moduleId) {
        log.info("Getting resources for module: {}", moduleId);
        List<ResourceDto> resources = resourceService.getResourcesByModule(moduleId);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<List<ResourceDto>> getResourcesByLesson(@PathVariable Long lessonId) {
        log.info("Getting resources for lesson: {}", lessonId);
        List<ResourceDto> resources = resourceService.getResourcesByLesson(lessonId);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping
    public ResponseEntity<List<ResourceDto>> getAllResources() {
        log.info("Getting all resources");
        List<ResourceDto> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/paged")
    public ResponseEntity<Page<ResourceDto>> getAllResourcesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting all resources - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDto> resources = resourceService.getAllResources(pageable);
        return ResponseEntity.ok(resources);
    }
    
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        log.info("Deleting resource: {}", resourceId);
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }
    
    // File Upload
    @PostMapping("/upload/courses/{courseId}")
    public ResponseEntity<ResourceDto> uploadFileForCourse(@PathVariable Long courseId,
                                                        @RequestParam("file") MultipartFile file) {
        log.info("Uploading file for course: {}", courseId);
        ResourceDto resource = resourceService.uploadFile(file, courseId, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
    
    @PostMapping("/upload/modules/{moduleId}")
    public ResponseEntity<ResourceDto> uploadFileForModule(@PathVariable Long moduleId,
                                                        @RequestParam("file") MultipartFile file) {
        log.info("Uploading file for module: {}", moduleId);
        ResourceDto resource = resourceService.uploadFile(file, null, moduleId, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
    
    @PostMapping("/upload/lessons/{lessonId}")
    public ResponseEntity<ResourceDto> uploadFileForLesson(@PathVariable Long lessonId,
                                                        @RequestParam("file") MultipartFile file) {
        log.info("Uploading file for lesson: {}", lessonId);
        ResourceDto resource = resourceService.uploadFile(file, null, null, lessonId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
    
    // Resource Search and Filtering
    @GetMapping("/search")
    public ResponseEntity<List<ResourceDto>> searchResources(@RequestParam String keyword) {
        log.info("Searching resources with keyword: {}", keyword);
        List<ResourceDto> resources = resourceService.searchResources(keyword);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/search/courses/{courseId}")
    public ResponseEntity<List<ResourceDto>> searchResourcesByCourse(@PathVariable Long courseId,
                                                                  @RequestParam String keyword) {
        log.info("Searching resources for course: {} with keyword: {}", courseId, keyword);
        List<ResourceDto> resources = resourceService.searchResourcesByCourse(courseId, keyword);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/file-type/{fileType}")
    public ResponseEntity<List<ResourceDto>> getResourcesByFileType(@PathVariable String fileType) {
        log.info("Getting resources by file type: {}", fileType);
        List<ResourceDto> resources = resourceService.getResourcesByFileType(fileType);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/mime-type/{mimeType}")
    public ResponseEntity<List<ResourceDto>> getResourcesByMimeType(@PathVariable String mimeType) {
        log.info("Getting resources by MIME type: {}", mimeType);
        List<ResourceDto> resources = resourceService.getResourcesByMimeType(mimeType);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/public")
    public ResponseEntity<List<ResourceDto>> getPublicResources() {
        log.info("Getting public resources");
        List<ResourceDto> resources = resourceService.getPublicResources();
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/private")
    public ResponseEntity<List<ResourceDto>> getPrivateResources() {
        log.info("Getting private resources");
        List<ResourceDto> resources = resourceService.getPrivateResources();
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/largest")
    public ResponseEntity<List<ResourceDto>> getLargestResources() {
        log.info("Getting largest resources");
        List<ResourceDto> resources = resourceService.getLargestResources();
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/largest/courses/{courseId}")
    public ResponseEntity<List<ResourceDto>> getLargestResourcesByCourse(@PathVariable Long courseId) {
        log.info("Getting largest resources for course: {}", courseId);
        List<ResourceDto> resources = resourceService.getLargestResourcesByCourse(courseId);
        return ResponseEntity.ok(resources);
    }
    
    // Resource Statistics
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getResourceCount() {
        log.info("Getting resource count");
        Long count = resourceService.getResourceCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/count/courses/{courseId}")
    public ResponseEntity<Long> getResourceCountByCourse(@PathVariable Long courseId) {
        log.info("Getting resource count for course: {}", courseId);
        Long count = resourceService.getResourceCountByCourse(courseId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/count/file-type/{fileType}")
    public ResponseEntity<Long> getResourceCountByFileType(@PathVariable String fileType) {
        log.info("Getting resource count by file type: {}", fileType);
        Long count = resourceService.getResourceCountByFileType(fileType);
        return ResponseEntity.ok(count);
    }
    
    // Health Check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Resource Service is running");
    }
} 