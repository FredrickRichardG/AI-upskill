package com.lms.assessmentservice.service;

import com.lms.assessmentservice.model.Quiz;
import com.lms.assessmentservice.model.Submission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeQuizService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;
    
    // Quiz session management
    public void startQuizSession(Long quizId, Long courseId, Long userId, String username) {
        Map<String, Object> sessionData = Map.of(
                "quizId", quizId,
                "courseId", courseId,
                "userId", userId,
                "username", username,
                "status", "STARTED",
                "startTime", LocalDateTime.now(),
                "sessionId", UUID.randomUUID().toString()
        );
        
        // Store session in Redis
        String sessionKey = "quiz_session:" + quizId + ":" + userId;
        redisTemplate.opsForValue().set(sessionKey, sessionData);
        
        // Notify other participants
        String destination = "/topic/quiz/" + quizId;
        messagingTemplate.convertAndSend(destination, sessionData);
        
        log.info("Quiz session started: user {} for quiz {}", username, quizId);
    }
    
    public void updateQuizProgress(Long quizId, Long userId, String username, 
                                 Integer questionNumber, String answer, Integer timeRemaining) {
        Map<String, Object> progressData = Map.of(
                "quizId", quizId,
                "userId", userId,
                "username", username,
                "questionNumber", questionNumber,
                "answer", answer,
                "timeRemaining", timeRemaining,
                "timestamp", LocalDateTime.now()
        );
        
        // Store progress in Redis
        String progressKey = "quiz_progress:" + quizId + ":" + userId;
        redisTemplate.opsForValue().set(progressKey, progressData);
        
        // Send to WebSocket subscribers
        String destination = "/topic/quiz-progress/" + quizId;
        messagingTemplate.convertAndSend(destination, progressData);
        
        log.info("Quiz progress updated: user {} question {} for quiz {}", 
                username, questionNumber, quizId);
    }
    
    public void submitQuizAnswer(Long quizId, Long userId, String username, 
                               Map<String, Object> answers, Double score) {
        Map<String, Object> submissionData = Map.of(
                "quizId", quizId,
                "userId", userId,
                "username", username,
                "answers", answers,
                "score", score,
                "submittedAt", LocalDateTime.now(),
                "status", "SUBMITTED"
        );
        
        // Store submission in Redis
        String submissionKey = "quiz_submission:" + quizId + ":" + userId;
        redisTemplate.opsForValue().set(submissionKey, submissionData);
        
        // Send to WebSocket subscribers
        String destination = "/topic/quiz-submissions/" + quizId;
        messagingTemplate.convertAndSend(destination, submissionData);
        
        // Send notification
        if (score >= 70.0) {
            notificationService.sendQuizCompletionNotification(userId, quizId, score, true);
        } else {
            notificationService.sendQuizFailureNotification(userId, quizId, score);
        }
        
        log.info("Quiz submitted: user {} score {} for quiz {}", username, score, quizId);
    }
    
    public void endQuizSession(Long quizId, Long courseId) {
        // Get all active sessions for this quiz
        String pattern = "quiz_session:" + quizId + ":*";
        java.util.Set<String> sessions = redisTemplate.keys(pattern);
        
        Map<String, Object> endData = Map.of(
                "quizId", quizId,
                "courseId", courseId,
                "status", "ENDED",
                "endTime", LocalDateTime.now(),
                "participantCount", sessions != null ? sessions.size() : 0
        );
        
        // Send to WebSocket subscribers
        String destination = "/topic/quiz-end/" + quizId;
        messagingTemplate.convertAndSend(destination, endData);
        
        // Send results to course subscribers
        String courseDestination = "/topic/quiz-results/" + courseId;
        messagingTemplate.convertAndSend(courseDestination, endData);
        
        log.info("Quiz session ended: quiz {} with {} participants", quizId, 
                sessions != null ? sessions.size() : 0);
    }
    
    // Real-time quiz analytics
    public void sendQuizAnalytics(Long quizId, Long courseId) {
        // Get all submissions for this quiz
        String pattern = "quiz_submission:" + quizId + ":*";
        java.util.Set<String> submissions = redisTemplate.keys(pattern);
        
        if (submissions != null && !submissions.isEmpty()) {
            Map<String, Object> analytics = Map.of(
                    "quizId", quizId,
                    "courseId", courseId,
                    "totalSubmissions", submissions.size(),
                    "averageScore", calculateAverageScore(quizId),
                    "timestamp", LocalDateTime.now()
            );
            
            // Send to WebSocket subscribers
            String destination = "/topic/quiz-analytics/" + quizId;
            messagingTemplate.convertAndSend(destination, analytics);
            
            log.info("Quiz analytics sent for quiz: {}", quizId);
        }
    }
    
    private Double calculateAverageScore(Long quizId) {
        String pattern = "quiz_submission:" + quizId + ":*";
        java.util.Set<String> submissions = redisTemplate.keys(pattern);
        
        if (submissions == null || submissions.isEmpty()) {
            return 0.0;
        }
        
        double totalScore = 0.0;
        int count = 0;
        
        for (String key : submissions) {
            Object submission = redisTemplate.opsForValue().get(key);
            if (submission instanceof Map) {
                Map<String, Object> submissionMap = (Map<String, Object>) submission;
                Object scoreObj = submissionMap.get("score");
                if (scoreObj instanceof Number) {
                    totalScore += ((Number) scoreObj).doubleValue();
                    count++;
                }
            }
        }
        
        return count > 0 ? totalScore / count : 0.0;
    }
    
    // Live quiz leaderboard
    public void updateLeaderboard(Long quizId, Long courseId) {
        String pattern = "quiz_submission:" + quizId + ":*";
        java.util.Set<String> submissions = redisTemplate.keys(pattern);
        
        if (submissions != null && !submissions.isEmpty()) {
            // Sort submissions by score (this would be more complex in production)
            Map<String, Object> leaderboardData = Map.of(
                    "quizId", quizId,
                    "courseId", courseId,
                    "participantCount", submissions.size(),
                    "timestamp", LocalDateTime.now()
            );
            
            // Send to WebSocket subscribers
            String destination = "/topic/quiz-leaderboard/" + quizId;
            messagingTemplate.convertAndSend(destination, leaderboardData);
            
            log.info("Leaderboard updated for quiz: {}", quizId);
        }
    }
    
    // Quiz time synchronization
    public void syncQuizTime(Long quizId, Integer timeRemaining) {
        Map<String, Object> timeData = Map.of(
                "quizId", quizId,
                "timeRemaining", timeRemaining,
                "timestamp", LocalDateTime.now()
        );
        
        // Send to all quiz participants
        String destination = "/topic/quiz-time/" + quizId;
        messagingTemplate.convertAndSend(destination, timeData);
        
        log.info("Quiz time synced: {} seconds remaining for quiz {}", timeRemaining, quizId);
    }
} 