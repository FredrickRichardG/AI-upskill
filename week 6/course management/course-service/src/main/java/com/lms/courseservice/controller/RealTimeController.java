package com.lms.courseservice.controller;

import com.lms.courseservice.model.*;
import com.lms.courseservice.service.RealTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
@Slf4j
public class RealTimeController {
    
    private final RealTimeService realTimeService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Announcement endpoints
    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        log.info("Creating announcement: {}", announcement.getTitle());
        realTimeService.sendAnnouncement(announcement);
        return ResponseEntity.ok(announcement);
    }
    
    @GetMapping("/announcements/{courseId}")
    public ResponseEntity<List<Object>> getRecentAnnouncements(@PathVariable Long courseId) {
        String redisKey = "announcements:" + courseId;
        List<Object> announcements = redisTemplate.opsForList().range(redisKey, 0, 49);
        return ResponseEntity.ok(announcements);
    }
    
    // Quiz participation endpoints
    @PostMapping("/quiz/join")
    public ResponseEntity<QuizParticipation> joinQuiz(@RequestBody QuizParticipation participation) {
        log.info("User {} joining quiz {}", participation.getUsername(), participation.getQuizId());
        realTimeService.sendQuizParticipation(participation);
        return ResponseEntity.ok(participation);
    }
    
    @PostMapping("/quiz/update")
    public ResponseEntity<QuizParticipation> updateQuizParticipation(@RequestBody QuizParticipation participation) {
        log.info("Updating quiz participation for user {} in quiz {}", 
                participation.getUsername(), participation.getQuizId());
        realTimeService.sendQuizParticipation(participation);
        return ResponseEntity.ok(participation);
    }
    
    @PostMapping("/quiz/{quizId}/results")
    public ResponseEntity<Object> getQuizResults(@PathVariable Long quizId, @RequestParam Long courseId) {
        log.info("Getting quiz results for quiz: {}", quizId);
        realTimeService.sendQuizResults(quizId, courseId);
        return ResponseEntity.ok().build();
    }
    
    // Progress notification endpoints
    @PostMapping("/notifications")
    public ResponseEntity<ProgressNotification> sendNotification(@RequestBody ProgressNotification notification) {
        log.info("Sending notification: {} to user {}", notification.getTitle(), notification.getUserId());
        realTimeService.sendProgressNotification(notification);
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Object>> getUserNotifications(@PathVariable Long userId) {
        String redisKey = "notifications:" + userId;
        List<Object> notifications = redisTemplate.opsForList().range(redisKey, 0, 99);
        return ResponseEntity.ok(notifications);
    }
    
    // Course notifications
    @PostMapping("/courses/{courseId}/notifications")
    public ResponseEntity<Object> sendCourseNotification(@PathVariable Long courseId, 
                                                      @RequestBody Map<String, Object> notificationData) {
        String title = (String) notificationData.get("title");
        String message = (String) notificationData.get("message");
        Object data = notificationData.get("data");
        
        log.info("Sending course notification: {} for course {}", title, courseId);
        realTimeService.sendCourseNotification(courseId, title, message, data);
        return ResponseEntity.ok().build();
    }
    
    // Lesson progress
    @PostMapping("/lessons/{lessonId}/progress")
    public ResponseEntity<Object> updateLessonProgress(@PathVariable Long lessonId,
                                                    @RequestParam Long courseId,
                                                    @RequestParam Long userId,
                                                    @RequestParam String username,
                                                    @RequestParam Integer progressPercentage) {
        log.info("Updating lesson progress: user {} at {}% for lesson {}", 
                username, progressPercentage, lessonId);
        realTimeService.sendLessonProgress(courseId, lessonId, userId, username, progressPercentage);
        return ResponseEntity.ok().build();
    }
    
    // Participant count
    @PostMapping("/courses/{courseId}/participants")
    public ResponseEntity<Object> updateParticipantCount(@PathVariable Long courseId,
                                                       @RequestParam Integer participantCount) {
        log.info("Updating participant count: {} for course {}", participantCount, courseId);
        realTimeService.updateParticipantCount(courseId, participantCount);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/courses/{courseId}/participants")
    public ResponseEntity<Object> getParticipantCount(@PathVariable Long courseId) {
        String redisKey = "participants:" + courseId;
        Object count = redisTemplate.opsForValue().get(redisKey);
        return ResponseEntity.ok(count);
    }
    
    // Chat history
    @GetMapping("/chat/{courseId}/history")
    public ResponseEntity<List<Object>> getChatHistory(@PathVariable Long courseId) {
        String redisKey = "chat:" + courseId;
        List<Object> messages = redisTemplate.opsForList().range(redisKey, 0, 99);
        return ResponseEntity.ok(messages);
    }
    
    // Active quiz participations
    @GetMapping("/quiz/{quizId}/participations")
    public ResponseEntity<Object> getQuizParticipations(@PathVariable Long quizId) {
        String redisKey = "quiz_participations:" + quizId;
        Object participations = redisTemplate.opsForHash().entries(redisKey);
        return ResponseEntity.ok(participations);
    }
} 