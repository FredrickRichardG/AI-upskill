package com.lms.courseservice.service;

import com.lms.courseservice.config.MessagingConfig;
import com.lms.courseservice.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Chat functionality
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Send to WebSocket subscribers
        String destination = "/topic/chat/" + chatMessage.getCourseId();
        messagingTemplate.convertAndSend(destination, chatMessage);
        
        // Send to RabbitMQ for persistence
        rabbitTemplate.convertAndSend(MessagingConfig.CHAT_EXCHANGE, 
                                    MessagingConfig.CHAT_ROUTING_KEY, chatMessage);
        
        log.info("Chat message sent: {}", chatMessage.getMessage());
    }
    
    public void sendUserJoinMessage(Long courseId, Long userId, String username) {
        ChatMessage joinMessage = ChatMessage.builder()
                .courseId(courseId)
                .userId(userId)
                .username(username)
                .message(username + " joined the chat")
                .type(ChatMessage.MessageType.JOIN)
                .build();
        
        sendChatMessage(joinMessage);
    }
    
    public void sendUserLeaveMessage(Long courseId, Long userId, String username) {
        ChatMessage leaveMessage = ChatMessage.builder()
                .courseId(courseId)
                .userId(userId)
                .username(username)
                .message(username + " left the chat")
                .type(ChatMessage.MessageType.LEAVE)
                .build();
        
        sendChatMessage(leaveMessage);
    }
    
    // Announcement functionality
    public void sendAnnouncement(Announcement announcement) {
        announcement.setId(UUID.randomUUID().toString());
        announcement.setTimestamp(LocalDateTime.now());
        
        // Send to WebSocket subscribers
        String destination = "/topic/announcements/" + announcement.getCourseId();
        messagingTemplate.convertAndSend(destination, announcement);
        
        // Send to RabbitMQ for persistence
        rabbitTemplate.convertAndSend(MessagingConfig.ANNOUNCEMENT_EXCHANGE, 
                                    MessagingConfig.ANNOUNCEMENT_ROUTING_KEY, announcement);
        
        // Store in Redis for recent announcements
        String redisKey = "announcements:" + announcement.getCourseId();
        redisTemplate.opsForList().leftPush(redisKey, announcement);
        redisTemplate.opsForList().trim(redisKey, 0, 49); // Keep last 50 announcements
        
        log.info("Announcement sent: {}", announcement.getTitle());
    }
    
    // Quiz participation functionality
    public void sendQuizParticipation(QuizParticipation participation) {
        participation.setId(UUID.randomUUID().toString());
        
        // Send to WebSocket subscribers
        String destination = "/topic/quiz/" + participation.getQuizId();
        messagingTemplate.convertAndSend(destination, participation);
        
        // Send to RabbitMQ for processing
        rabbitTemplate.convertAndSend(MessagingConfig.QUIZ_EXCHANGE, 
                                    MessagingConfig.QUIZ_ROUTING_KEY, participation);
        
        // Store in Redis for active participations
        String redisKey = "quiz_participations:" + participation.getQuizId();
        redisTemplate.opsForHash().put(redisKey, participation.getUserId().toString(), participation);
        
        log.info("Quiz participation updated: user {} for quiz {}", 
                participation.getUsername(), participation.getQuizId());
    }
    
    public void sendQuizResults(Long quizId, Long courseId) {
        // Get all participations from Redis
        String redisKey = "quiz_participations:" + quizId;
        java.util.Collection<Object> participations = redisTemplate.opsForHash().values(redisKey);
        
        // Send results to course subscribers
        String destination = "/topic/quiz-results/" + courseId;
        messagingTemplate.convertAndSend(destination, participations);
        
        log.info("Quiz results sent for quiz: {}", quizId);
    }
    
    // Progress notification functionality
    public void sendProgressNotification(ProgressNotification notification) {
        notification.setId(UUID.randomUUID().toString());
        notification.setTimestamp(LocalDateTime.now());
        notification.setIsRead(false);
        
        // Send to specific user via WebSocket
        String userDestination = "/user/" + notification.getUserId() + "/queue/notifications";
        messagingTemplate.convertAndSendToUser(notification.getUserId().toString(), 
                                             "/queue/notifications", notification);
        
        // Send to RabbitMQ for persistence
        rabbitTemplate.convertAndSend(MessagingConfig.PROGRESS_EXCHANGE, 
                                    MessagingConfig.PROGRESS_ROUTING_KEY, notification);
        
        // Store in Redis for user notifications
        String redisKey = "notifications:" + notification.getUserId();
        redisTemplate.opsForList().leftPush(redisKey, notification);
        redisTemplate.opsForList().trim(redisKey, 0, 99); // Keep last 100 notifications
        
        log.info("Progress notification sent: {} to user {}", 
                notification.getTitle(), notification.getUserId());
    }
    
    // Course-wide notifications
    public void sendCourseNotification(Long courseId, String title, String message, Object data) {
        ProgressNotification notification = ProgressNotification.builder()
                .courseId(courseId)
                .title(title)
                .message(message)
                .type(ProgressNotification.NotificationType.GENERAL)
                .data(data)
                .build();
        
        // Send to all course subscribers
        String destination = "/topic/course-notifications/" + courseId;
        messagingTemplate.convertAndSend(destination, notification);
        
        log.info("Course notification sent: {} for course {}", title, courseId);
    }
    
    // Real-time lesson progress
    public void sendLessonProgress(Long courseId, Long lessonId, Long userId, 
                                 String username, Integer progressPercentage) {
        Map<String, Object> progressData = Map.of(
                "userId", userId,
                "username", username,
                "lessonId", lessonId,
                "progressPercentage", progressPercentage,
                "timestamp", LocalDateTime.now()
        );
        
        String destination = "/topic/lesson-progress/" + courseId;
        messagingTemplate.convertAndSend(destination, progressData);
        
        log.info("Lesson progress sent: user {} at {}% for lesson {}", 
                username, progressPercentage, lessonId);
    }
    
    // Live participant count
    public void updateParticipantCount(Long courseId, Integer participantCount) {
        Map<String, Object> countData = Map.of(
                "courseId", courseId,
                "participantCount", participantCount,
                "timestamp", LocalDateTime.now()
        );
        
        String destination = "/topic/participants/" + courseId;
        messagingTemplate.convertAndSend(destination, countData);
        
        // Store in Redis for quick access
        String redisKey = "participants:" + courseId;
        redisTemplate.opsForValue().set(redisKey, participantCount);
        
        log.info("Participant count updated: {} for course {}", participantCount, courseId);
    }
} 