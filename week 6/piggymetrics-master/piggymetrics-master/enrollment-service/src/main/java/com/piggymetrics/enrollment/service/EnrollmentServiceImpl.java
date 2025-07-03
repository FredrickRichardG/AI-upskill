package com.piggymetrics.enrollment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.enrollment.domain.Enrollment;
import com.piggymetrics.enrollment.domain.OutboxEvent;
import com.piggymetrics.enrollment.domain.Progress;
import com.piggymetrics.enrollment.events.EnrollmentCreatedEvent;
import com.piggymetrics.enrollment.events.ProgressUpdatedEvent;
import com.piggymetrics.enrollment.repository.EnrollmentRepository;
import com.piggymetrics.enrollment.repository.OutboxEventRepository;
import com.piggymetrics.enrollment.repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private ProgressRepository progressRepository;
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Enrollment> enroll(Long userId, Long courseId) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = new Enrollment();
            enrollment.setUserId(userId);
            enrollment.setCourseId(courseId);
            enrollment.setStatus("ENROLLED");
            Enrollment saved = enrollmentRepository.save(enrollment);
            // Write to outbox
            EnrollmentCreatedEvent event = new EnrollmentCreatedEvent(saved.getId(), userId, courseId);
            OutboxEvent outbox = new OutboxEvent();
            outbox.setEventType("enrollment.created");
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outboxEventRepository.save(outbox);
            return saved;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Progress> markLessonComplete(Long enrollmentId, Long lessonId) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
            Progress progress = new Progress();
            progress.setEnrollment(enrollment);
            progress.setLessonId(lessonId);
            progress.setCompletedTs(Instant.now());
            Progress saved = progressRepository.save(progress);
            // Write to outbox
            // For demo, progressPct is set to 100.0 (real logic would compute actual progress)
            ProgressUpdatedEvent event = new ProgressUpdatedEvent(enrollment.getUserId(), enrollment.getCourseId(), 100.0);
            OutboxEvent outbox = new OutboxEvent();
            outbox.setEventType("progress.updated");
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outboxEventRepository.save(outbox);
            return saved;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Enrollment> getEnrollment(Long enrollmentId) {
        return Mono.fromCallable(() -> enrollmentRepository.findById(enrollmentId).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Progress> getProgress(Long enrollmentId, Long lessonId) {
        return Mono.fromCallable(() -> progressRepository.findAll().stream()
                .filter(p -> p.getEnrollment().getId().equals(enrollmentId) && p.getLessonId().equals(lessonId))
                .findFirst().orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<Progress>> getAllProgress(Long enrollmentId) {
        return Mono.fromCallable(() -> progressRepository.findAll().stream()
                .filter(p -> p.getEnrollment().getId().equals(enrollmentId))
                .collect(Collectors.toList()))
                .subscribeOn(Schedulers.boundedElastic());
    }
} 