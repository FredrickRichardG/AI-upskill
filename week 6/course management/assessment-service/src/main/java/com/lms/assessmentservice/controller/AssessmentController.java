package com.lms.assessmentservice.controller;

import com.lms.assessmentservice.model.Quiz;
import com.lms.assessmentservice.model.Submission;
import com.lms.assessmentservice.model.SubmissionStatus;
import com.lms.assessmentservice.repository.QuizRepository;
import com.lms.assessmentservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AssessmentController {
    private final QuizRepository quizRepository;
    private final SubmissionRepository submissionRepository;

    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<Submission> submitQuiz(@PathVariable Long quizId, @RequestBody Submission submission) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isPresent()) {
            submission.setQuiz(quizOpt.get());
            submission.setStatus(SubmissionStatus.SUBMITTED);
            submission.setGradedTs(Instant.now());
            Submission saved = submissionRepository.save(submission);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/submissions/{id}")
    @CircuitBreaker(name = "assessmentServiceCB", fallbackMethod = "getSubmissionFallback")
    public ResponseEntity<Submission> getSubmission(@PathVariable Long id) {
        Optional<Submission> submission = submissionRepository.findById(id);
        return submission.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Submission> getSubmissionFallback(Long id, Throwable t) {
        return ResponseEntity.status(503).build();
    }

    @PostMapping("/assignments/{id}/grade")
    public ResponseEntity<Submission> manualGrade(@PathVariable Long id, @RequestBody Submission gradeUpdate) {
        Optional<Submission> submissionOpt = submissionRepository.findById(id);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();
            submission.setScore(gradeUpdate.getScore());
            submission.setStatus(SubmissionStatus.GRADED);
            submission.setGradedTs(Instant.now());
            submissionRepository.save(submission);
            return ResponseEntity.ok(submission);
        }
        return ResponseEntity.notFound().build();
    }
} 