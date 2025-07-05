package com.lms.courseservice.controller;

import com.lms.courseservice.dto.CourseCreateRequest;
import com.lms.courseservice.dto.CourseDto;
import com.lms.courseservice.dto.CourseSearchRequest;
import com.lms.courseservice.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    
    private final CourseService courseService;
    
    // Course CRUD Operations
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseCreateRequest request) {
        log.info("Creating course: {}", request.getTitle());
        CourseDto course = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }
    
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long courseId, 
                                                @RequestBody CourseCreateRequest request) {
        log.info("Updating course: {}", courseId);
        CourseDto course = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(course);
    }
    
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        log.info("Getting course by ID: {}", courseId);
        CourseDto course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CourseDto> getCourseBySlug(@PathVariable String slug) {
        log.info("Getting course by slug: {}", slug);
        CourseDto course = courseService.getCourseBySlug(slug);
        return ResponseEntity.ok(course);
    }
    
    @GetMapping
    public ResponseEntity<Page<CourseDto>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all courses - page: {}, size: {}, sort: {}", page, size, sortBy);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDto> courses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(courses);
    }
    
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        log.info("Deleting course: {}", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
    
    // Course Search and Filtering
    @PostMapping("/search")
    public ResponseEntity<Page<CourseDto>> searchCourses(@RequestBody CourseSearchRequest request) {
        log.info("Searching courses with criteria: {}", request);
        Page<CourseDto> courses = courseService.searchCourses(request);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<CourseDto>> searchCoursesByParams(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching courses with params - keyword: {}, category: {}, status: {}", 
                keyword, categoryId, status);
        
        CourseSearchRequest request = CourseSearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .status(status != null ? com.lms.courseservice.model.CourseStatus.valueOf(status) : null)
                .difficulty(difficulty)
                .minPrice(minPrice != null ? java.math.BigDecimal.valueOf(minPrice) : null)
                .maxPrice(maxPrice != null ? java.math.BigDecimal.valueOf(maxPrice) : null)
                .pageable(PageRequest.of(page, size))
                .build();
        
        Page<CourseDto> courses = courseService.searchCourses(request);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseDto>> getCoursesByCategory(@PathVariable Long categoryId) {
        log.info("Getting courses by category: {}", categoryId);
        List<CourseDto> courses = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseDto>> getCoursesByInstructor(@PathVariable Long instructorId) {
        log.info("Getting courses by instructor: {}", instructorId);
        List<CourseDto> courses = courseService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<CourseDto>> getPublishedCourses() {
        log.info("Getting published courses");
        List<CourseDto> courses = courseService.getPublishedCourses();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<CourseDto>> getFeaturedCourses() {
        log.info("Getting featured courses");
        List<CourseDto> courses = courseService.getFeaturedCourses();
        return ResponseEntity.ok(courses);
    }
    
    // Course Status Management
    @PostMapping("/{courseId}/publish")
    public ResponseEntity<CourseDto> publishCourse(@PathVariable Long courseId) {
        log.info("Publishing course: {}", courseId);
        CourseDto course = courseService.publishCourse(courseId);
        return ResponseEntity.ok(course);
    }
    
    @PostMapping("/{courseId}/unpublish")
    public ResponseEntity<CourseDto> unpublishCourse(@PathVariable Long courseId) {
        log.info("Unpublishing course: {}", courseId);
        CourseDto course = courseService.unpublishCourse(courseId);
        return ResponseEntity.ok(course);
    }
    
    // Course Statistics
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getCourseCount() {
        log.info("Getting course count");
        Long count = courseService.getCourseCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/published-count")
    public ResponseEntity<Long> getPublishedCourseCount() {
        log.info("Getting published course count");
        Long count = courseService.getPublishedCourseCount();
        return ResponseEntity.ok(count);
    }
    
    // Health Check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Course Service is running");
    }
} 