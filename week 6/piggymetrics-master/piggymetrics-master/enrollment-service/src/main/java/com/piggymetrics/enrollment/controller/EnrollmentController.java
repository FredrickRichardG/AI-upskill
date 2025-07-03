package com.piggymetrics.enrollment.controller;

import com.piggymetrics.enrollment.domain.Enrollment;
import com.piggymetrics.enrollment.domain.Progress;
import com.piggymetrics.enrollment.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ROLE_STUDENT')")
    public Mono<Enrollment> enroll(@RequestParam Long userId, @RequestParam Long courseId) {
        return enrollmentService.enroll(userId, courseId);
    }

    @PatchMapping("/{enrollmentId}/lessons/{lessonId}")
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ROLE_STUDENT')")
    public Mono<Progress> markLessonComplete(@PathVariable Long enrollmentId, @PathVariable Long lessonId) {
        return enrollmentService.markLessonComplete(enrollmentId, lessonId);
    }

    @GetMapping("/{enrollmentId}/progress/{lessonId}")
    public Mono<ResponseEntity<Progress>> getProgress(@PathVariable Long enrollmentId, @PathVariable Long lessonId) {
        return enrollmentService.getProgress(enrollmentId, lessonId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{enrollmentId}/progress")
    public Mono<ResponseEntity<List<Progress>>> getAllProgress(@PathVariable Long enrollmentId) {
        return enrollmentService.getAllProgress(enrollmentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 