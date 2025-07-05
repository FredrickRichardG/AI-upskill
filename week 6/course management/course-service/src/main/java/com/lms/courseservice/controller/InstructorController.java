package com.lms.courseservice.controller;

import com.lms.courseservice.dto.InstructorDto;
import com.lms.courseservice.service.InstructorService;
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
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
@Slf4j
public class InstructorController {
    
    private final InstructorService instructorService;
    
    // Instructor CRUD Operations
    @PostMapping
    public ResponseEntity<InstructorDto> createInstructor(@RequestBody InstructorDto request) {
        log.info("Creating instructor: {} {}", request.getFirstName(), request.getLastName());
        InstructorDto instructor = instructorService.createInstructor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(instructor);
    }
    
    @PutMapping("/{instructorId}")
    public ResponseEntity<InstructorDto> updateInstructor(@PathVariable Long instructorId, 
                                                        @RequestBody InstructorDto request) {
        log.info("Updating instructor: {}", instructorId);
        InstructorDto instructor = instructorService.updateInstructor(instructorId, request);
        return ResponseEntity.ok(instructor);
    }
    
    @GetMapping("/{instructorId}")
    public ResponseEntity<InstructorDto> getInstructorById(@PathVariable Long instructorId) {
        log.info("Getting instructor by ID: {}", instructorId);
        InstructorDto instructor = instructorService.getInstructorById(instructorId);
        return ResponseEntity.ok(instructor);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<InstructorDto> getInstructorByEmail(@PathVariable String email) {
        log.info("Getting instructor by email: {}", email);
        InstructorDto instructor = instructorService.getInstructorByEmail(email);
        return ResponseEntity.ok(instructor);
    }
    
    @GetMapping
    public ResponseEntity<List<InstructorDto>> getAllInstructors() {
        log.info("Getting all instructors");
        List<InstructorDto> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }
    
    @GetMapping("/paged")
    public ResponseEntity<Page<InstructorDto>> getAllInstructorsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting all instructors - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InstructorDto> instructors = instructorService.getAllInstructors(pageable);
        return ResponseEntity.ok(instructors);
    }
    
    @DeleteMapping("/{instructorId}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long instructorId) {
        log.info("Deleting instructor: {}", instructorId);
        instructorService.deleteInstructor(instructorId);
        return ResponseEntity.noContent().build();
    }
    
    // Instructor Search
    @GetMapping("/search")
    public ResponseEntity<List<InstructorDto>> searchInstructors(@RequestParam String keyword) {
        log.info("Searching instructors with keyword: {}", keyword);
        List<InstructorDto> instructors = instructorService.searchInstructors(keyword);
        return ResponseEntity.ok(instructors);
    }
    
    // Instructor Statistics
    @GetMapping("/with-course-count")
    public ResponseEntity<List<InstructorDto>> getInstructorsWithCourseCount() {
        log.info("Getting instructors with course count");
        List<InstructorDto> instructors = instructorService.getInstructorsWithCourseCount();
        return ResponseEntity.ok(instructors);
    }
    
    @GetMapping("/top")
    public ResponseEntity<List<InstructorDto>> getTopInstructors() {
        log.info("Getting top instructors");
        List<InstructorDto> instructors = instructorService.getTopInstructors();
        return ResponseEntity.ok(instructors);
    }
    
    @GetMapping("/with-published-courses")
    public ResponseEntity<List<InstructorDto>> getInstructorsWithPublishedCourses() {
        log.info("Getting instructors with published courses");
        List<InstructorDto> instructors = instructorService.getInstructorsWithPublishedCourses();
        return ResponseEntity.ok(instructors);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<InstructorDto>> getActiveInstructors() {
        log.info("Getting active instructors");
        List<InstructorDto> instructors = instructorService.getActiveInstructors();
        return ResponseEntity.ok(instructors);
    }
    
    // Instructor Filtering
    @GetMapping("/expertise/{expertise}")
    public ResponseEntity<List<InstructorDto>> getInstructorsByExpertise(@PathVariable String expertise) {
        log.info("Getting instructors by expertise: {}", expertise);
        List<InstructorDto> instructors = instructorService.getInstructorsByExpertise(expertise);
        return ResponseEntity.ok(instructors);
    }
    
    @GetMapping("/experience")
    public ResponseEntity<List<InstructorDto>> getInstructorsByExperience(
            @RequestParam Integer minExperience,
            @RequestParam Integer maxExperience) {
        log.info("Getting instructors by experience range: {} - {}", minExperience, maxExperience);
        List<InstructorDto> instructors = instructorService.getInstructorsByExperienceRange(minExperience, maxExperience);
        return ResponseEntity.ok(instructors);
    }
    
    // Instructor Statistics
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getInstructorCount() {
        log.info("Getting instructor count");
        Long count = instructorService.getInstructorCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveInstructorCount() {
        log.info("Getting active instructor count");
        Long count = instructorService.getActiveInstructorCount();
        return ResponseEntity.ok(count);
    }
    
    // Health Check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Instructor Service is running");
    }
} 