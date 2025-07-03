package com.piggymetrics.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.assessment.domain.OutboxEvent;
import com.piggymetrics.assessment.domain.Quiz;
import com.piggymetrics.assessment.domain.Submission;
import com.piggymetrics.assessment.events.GradeAwardedEvent;
import com.piggymetrics.assessment.repository.OutboxEventRepository;
import com.piggymetrics.assessment.repository.QuizRepository;
import com.piggymetrics.assessment.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Submission> submitQuiz(Long userId, Long quizId, String answersJson) {
        return Mono.fromCallable(() -> {
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
            Submission submission = new Submission();
            submission.setUserId(userId);
            submission.setQuiz(quiz);
            // In a real app, you would parse answersJson and compute score
            submission.setScore(0.0);
            submission.setStatus("SUBMITTED");
            submission.setGradedTs(null);
            return submissionRepository.save(submission);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Submission> getSubmission(Long submissionId) {
        return Mono.fromCallable(() -> submissionRepository.findById(submissionId).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Submission> gradeSubmission(Long submissionId, Double score) {
        return Mono.fromCallable(() -> {
            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
            submission.setScore(score);
            submission.setStatus("GRADED");
            submission.setGradedTs(Instant.now());
            Submission saved = submissionRepository.save(submission);
            // Write to outbox
            // For demo, courseId is set to null (real logic should fetch courseId from quiz)
            GradeAwardedEvent event = new GradeAwardedEvent(submission.getUserId(), null, score);
            OutboxEvent outbox = new OutboxEvent();
            outbox.setEventType("grade.awarded");
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outboxEventRepository.save(outbox);
            return saved;
        }).subscribeOn(Schedulers.boundedElastic());
    }
} 