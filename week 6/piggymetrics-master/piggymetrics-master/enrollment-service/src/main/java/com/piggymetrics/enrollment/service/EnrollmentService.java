package com.piggymetrics.enrollment.service;

import com.piggymetrics.enrollment.domain.Enrollment;
import com.piggymetrics.enrollment.domain.Progress;
import reactor.core.publisher.Mono;
import java.util.List;

public interface EnrollmentService {
    Mono<Enrollment> enroll(Long userId, Long courseId);
    Mono<Progress> markLessonComplete(Long enrollmentId, Long lessonId);
    Mono<Enrollment> getEnrollment(Long enrollmentId);
    Mono<Progress> getProgress(Long enrollmentId, Long lessonId);
    Mono<List<Progress>> getAllProgress(Long enrollmentId);
} 