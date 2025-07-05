package com.lms.courseservice.controller;

import com.lms.courseservice.model.*;
import com.lms.courseservice.service.RealTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final RealTimeService realTimeService;
    
    // Chat message handling
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{courseId}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        log.info("Received chat message: {}", chatMessage.getMessage());
        realTimeService.sendChatMessage(chatMessage);
        return chatMessage;
    }
    
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/chat/{courseId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
                             SimpMessageHeaderAccessor headerAccessor) {
        // Add username to web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getUsername());
        headerAccessor.getSessionAttributes().put("courseId", chatMessage.getCourseId());
        headerAccessor.getSessionAttributes().put("userId", chatMessage.getUserId());
        
        log.info("User {} joined chat for course {}", chatMessage.getUsername(), chatMessage.getCourseId());
        realTimeService.sendUserJoinMessage(chatMessage.getCourseId(), chatMessage.getUserId(), chatMessage.getUsername());
        
        return chatMessage;
    }
    
    // Quiz participation handling
    @MessageMapping("/quiz.join")
    @SendTo("/topic/quiz/{quizId}")
    public QuizParticipation joinQuiz(@Payload QuizParticipation participation) {
        log.info("User {} joined quiz {}", participation.getUsername(), participation.getQuizId());
        realTimeService.sendQuizParticipation(participation);
        return participation;
    }
    
    @MessageMapping("/quiz.update")
    @SendTo("/topic/quiz/{quizId}")
    public QuizParticipation updateQuizParticipation(@Payload QuizParticipation participation) {
        log.info("Quiz participation updated for user {} in quiz {}", 
                participation.getUsername(), participation.getQuizId());
        realTimeService.sendQuizParticipation(participation);
        return participation;
    }
    
    @MessageMapping("/quiz.complete")
    @SendTo("/topic/quiz/{quizId}")
    public QuizParticipation completeQuiz(@Payload QuizParticipation participation) {
        log.info("Quiz completed by user {} for quiz {}", 
                participation.getUsername(), participation.getQuizId());
        realTimeService.sendQuizParticipation(participation);
        return participation;
    }
    
    // Announcement handling
    @MessageMapping("/announcement.send")
    @SendTo("/topic/announcements/{courseId}")
    public Announcement sendAnnouncement(@Payload Announcement announcement) {
        log.info("Announcement sent by instructor {}: {}", 
                announcement.getInstructorName(), announcement.getTitle());
        realTimeService.sendAnnouncement(announcement);
        return announcement;
    }
    
    // Lesson progress handling
    @MessageMapping("/lesson.progress")
    @SendTo("/topic/lesson-progress/{courseId}")
    public Object updateLessonProgress(@Payload Object progressData) {
        log.info("Lesson progress updated: {}", progressData);
        return progressData;
    }
    
    // User-specific notifications
    @MessageMapping("/notifications.markRead")
    @SendToUser("/queue/notifications")
    public Object markNotificationRead(@Payload Object notificationData) {
        log.info("Notification marked as read: {}", notificationData);
        return notificationData;
    }
    
    // Course-wide notifications
    @MessageMapping("/course.notification")
    @SendTo("/topic/course-notifications/{courseId}")
    public Object sendCourseNotification(@Payload Object notificationData) {
        log.info("Course notification sent: {}", notificationData);
        return notificationData;
    }
    
    // Participant count updates
    @MessageMapping("/participants.update")
    @SendTo("/topic/participants/{courseId}")
    public Object updateParticipants(@Payload Object participantData) {
        log.info("Participant count updated: {}", participantData);
        return participantData;
    }
} 