package com.lms.enrollmentservice.controller;

import com.lms.enrollmentservice.model.Enrollment;
import com.lms.enrollmentservice.model.Progress;
import com.lms.enrollmentservice.repository.EnrollmentRepository;
import com.lms.enrollmentservice.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentRepository enrollmentRepository;
    private final ProgressRepository progressRepository;

    @PostMapping
    public ResponseEntity<Enrollment> enrollUser(@RequestBody Enrollment enrollment) {
        Enrollment saved = enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<List<Progress>> getProgress(@PathVariable Long id) {
        Optional<Enrollment> enrollment = enrollmentRepository.findById(id);
        if (enrollment.isPresent()) {
            List<Progress> progress = progressRepository.findAll().stream()
                .filter(p -> p.getEnrollment().getEnrollmentId().equals(id))
                .toList();
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/lessons/{lessonId}")
    public ResponseEntity<Progress> markLessonComplete(@PathVariable Long id, @PathVariable Long lessonId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);
        if (enrollmentOpt.isPresent()) {
            Progress progress = Progress.builder()
                .enrollment(enrollmentOpt.get())
                .lessonId(lessonId)
                .completedTs(Instant.now())
                .build();
            Progress saved = progressRepository.save(progress);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }
} 