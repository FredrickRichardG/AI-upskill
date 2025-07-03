package com.piggymetrics.assessment.controller;

import com.piggymetrics.assessment.domain.Submission;
import com.piggymetrics.assessment.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/quizzes/{quizId}/submit")
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ROLE_STUDENT')")
    public Mono<Submission> submitQuiz(@PathVariable Long quizId, @RequestParam Long userId, @RequestBody String answersJson) {
        return assessmentService.submitQuiz(userId, quizId, answersJson);
    }

    @GetMapping("/submissions/{id}")
    public Mono<ResponseEntity<Submission>> getSubmission(@PathVariable Long id) {
        return assessmentService.getSubmission(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/assignments/{id}/grade")
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ROLE_INSTRUCTOR')")
    public Mono<Submission> gradeSubmission(@PathVariable Long id, @RequestParam Double score) {
        return assessmentService.gradeSubmission(id, score);
    }
} 