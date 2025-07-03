package com.piggymetrics.assessment.service;

import com.piggymetrics.assessment.domain.Submission;
import reactor.core.publisher.Mono;

public interface AssessmentService {
    Mono<Submission> submitQuiz(Long userId, Long quizId, String answersJson);
    Mono<Submission> getSubmission(Long submissionId);
    Mono<Submission> gradeSubmission(Long submissionId, Double score);
} 