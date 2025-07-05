package com.lms.assessmentservice.service;

import com.lms.assessmentservice.model.Quiz;
import com.lms.assessmentservice.model.Submission;
import com.lms.assessmentservice.model.SubmissionStatus;
import com.lms.assessmentservice.repository.QuizRepository;
import com.lms.assessmentservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentAnalyticsService {
    
    private final SubmissionRepository submissionRepository;
    private final QuizRepository quizRepository;
    private final NotificationService notificationService;
    
    /**
     * Get user performance analytics for a course
     */
    @Cacheable(value = "assessment-analytics", key = "#userId + '_' + #courseId")
    public Map<String, Object> getUserCourseAnalytics(Long userId, Long courseId) {
        log.info("Getting assessment analytics for user {} in course {}", userId, courseId);
        
        List<Submission> userSubmissions = submissionRepository.findByUserIdAndCourseId(userId, courseId);
        
        int totalQuizzes = userSubmissions.size();
        int passedQuizzes = (int) userSubmissions.stream()
                .filter(sub -> SubmissionStatus.PASSED.equals(sub.getStatus()))
                .count();
        
        double averageScore = userSubmissions.stream()
                .mapToDouble(Submission::getScore)
                .average()
                .orElse(0.0);
        
        double averageTimeSpent = userSubmissions.stream()
                .filter(sub -> sub.getTimeSpent() != null)
                .mapToDouble(sub -> sub.getTimeSpent())
                .average()
                .orElse(0.0);
        
        int totalAttempts = userSubmissions.stream()
                .mapToInt(Submission::getAttemptNumber)
                .sum();
        
        return Map.of(
                "userId", userId,
                "courseId", courseId,
                "totalQuizzes", totalQuizzes,
                "passedQuizzes", passedQuizzes,
                "failedQuizzes", totalQuizzes - passedQuizzes,
                "passRate", totalQuizzes > 0 ? (double) passedQuizzes / totalQuizzes * 100 : 0.0,
                "averageScore", averageScore,
                "averageTimeSpent", averageTimeSpent,
                "totalAttempts", totalAttempts,
                "averageAttemptsPerQuiz", totalQuizzes > 0 ? (double) totalAttempts / totalQuizzes : 0.0
        );
    }
    
    /**
     * Get quiz performance analytics
     */
    @Cacheable(value = "quiz-analytics", key = "#quizId")
    public Map<String, Object> getQuizAnalytics(Long quizId) {
        log.info("Getting analytics for quiz {}", quizId);
        
        List<Submission> submissions = submissionRepository.findByQuizId(quizId);
        
        int totalSubmissions = submissions.size();
        int passedSubmissions = (int) submissions.stream()
                .filter(sub -> SubmissionStatus.PASSED.equals(sub.getStatus()))
                .count();
        
        double averageScore = submissions.stream()
                .mapToDouble(Submission::getScore)
                .average()
                .orElse(0.0);
        
        double averageTimeSpent = submissions.stream()
                .filter(sub -> sub.getTimeSpent() != null)
                .mapToDouble(sub -> sub.getTimeSpent())
                .average()
                .orElse(0.0);
        
        return Map.of(
                "quizId", quizId,
                "totalSubmissions", totalSubmissions,
                "passedSubmissions", passedSubmissions,
                "failedSubmissions", totalSubmissions - passedSubmissions,
                "passRate", totalSubmissions > 0 ? (double) passedSubmissions / totalSubmissions * 100 : 0.0,
                "averageScore", averageScore,
                "averageTimeSpent", averageTimeSpent
        );
    }
    
    /**
     * Get user's quiz attempt history
     */
    public List<Map<String, Object>> getUserQuizHistory(Long userId, Long quizId) {
        log.info("Getting quiz history for user {} and quiz {}", userId, quizId);
        
        List<Submission> submissions = submissionRepository.findByUserIdAndQuizId(userId, quizId);
        
        return submissions.stream()
                .map(sub -> {
                    Map<String, Object> history = new java.util.HashMap<>();
                    history.put("submissionId", sub.getSubmissionId());
                    history.put("attemptNumber", sub.getAttemptNumber());
                    history.put("score", sub.getScore());
                    history.put("status", sub.getStatus());
                    history.put("submittedAt", sub.getSubmittedAt());
                    history.put("timeSpent", sub.getTimeSpent());
                    history.put("isPassed", sub.getIsPassed());
                    return history;
                })
                .toList();
    }
    
    /**
     * Get course assessment summary
     */
    @Cacheable(value = "course-assessment-summary", key = "#courseId")
    public Map<String, Object> getCourseAssessmentSummary(Long courseId) {
        log.info("Getting assessment summary for course {}", courseId);
        
        List<Quiz> courseQuizzes = quizRepository.findByCourseId(courseId);
        List<Submission> courseSubmissions = submissionRepository.findByCourseId(courseId);
        
        int totalQuizzes = courseQuizzes.size();
        int totalSubmissions = courseSubmissions.size();
        int totalParticipants = (int) courseSubmissions.stream()
                .map(Submission::getUserId)
                .distinct()
                .count();
        
        double averagePassRate = courseQuizzes.stream()
                .mapToDouble(quiz -> {
                    List<Submission> quizSubmissions = submissionRepository.findByQuizId(quiz.getQuizId());
                    long passedCount = quizSubmissions.stream()
                            .filter(sub -> SubmissionStatus.PASSED.equals(sub.getStatus()))
                            .count();
                    return quizSubmissions.isEmpty() ? 0.0 : (double) passedCount / quizSubmissions.size() * 100;
                })
                .average()
                .orElse(0.0);
        
        return Map.of(
                "courseId", courseId,
                "totalQuizzes", totalQuizzes,
                "totalSubmissions", totalSubmissions,
                "totalParticipants", totalParticipants,
                "averagePassRate", averagePassRate,
                "activeQuizzes", (int) courseQuizzes.stream().filter(Quiz::getIsActive).count()
        );
    }
    
    /**
     * Async notification for quiz completion
     */
    @Async
    public CompletableFuture<Void> notifyQuizCompleted(Long userId, Long quizId, Double score, Boolean isPassed) {
        log.info("Sending quiz completion notification for user {} quiz {} score {} passed {}", 
                userId, quizId, score, isPassed);
        
        // TODO: Implement notification logic
        // This would integrate with the notification service
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Async notification for quiz failure
     */
    @Async
    public CompletableFuture<Void> notifyQuizFailed(Long userId, Long quizId, Double score) {
        log.info("Sending quiz failure notification for user {} quiz {} score {}", userId, quizId, score);
        
        // TODO: Implement notification logic
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Get learning recommendations based on assessment performance
     */
    public List<String> getLearningRecommendations(Long userId, Long courseId) {
        log.info("Getting learning recommendations for user {} in course {}", userId, courseId);
        
        Map<String, Object> analytics = getUserCourseAnalytics(userId, courseId);
        double passRate = (Double) analytics.get("passRate");
        double averageScore = (Double) analytics.get("averageScore");
        
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (passRate < 70.0) {
            recommendations.add("Consider reviewing course materials before taking quizzes");
            recommendations.add("Take practice quizzes to improve understanding");
        }
        
        if (averageScore < 75.0) {
            recommendations.add("Focus on areas where you scored lower");
            recommendations.add("Consider seeking help from instructors");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Great job! Keep up the excellent work");
        }
        
        return recommendations;
    }
} 