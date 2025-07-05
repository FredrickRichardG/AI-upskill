package com.lms.assessmentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    public void sendQuizCompletionNotification(Long userId, Long quizId, Double score, Boolean isPassed) {
        log.info("Sending quiz completion notification for user {} quiz {} score {} passed {}", userId, quizId, score, isPassed);
        // TODO: Implement actual notification logic
    }
    public void sendQuizFailureNotification(Long userId, Long quizId, Double score) {
        log.info("Sending quiz failure notification for user {} quiz {} score {}", userId, quizId, score);
        // TODO: Implement actual notification logic
    }
} 