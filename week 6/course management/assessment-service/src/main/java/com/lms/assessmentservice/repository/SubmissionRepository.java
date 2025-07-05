package com.lms.assessmentservice.repository;

import com.lms.assessmentservice.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    @Query("SELECT s FROM Submission s WHERE s.userId = :userId AND s.quiz.courseId = :courseId")
    List<Submission> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT s FROM Submission s WHERE s.quiz.id = :quizId")
    List<Submission> findByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT s FROM Submission s WHERE s.userId = :userId AND s.quiz.id = :quizId")
    List<Submission> findByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);
    
    @Query("SELECT s FROM Submission s WHERE s.quiz.courseId = :courseId")
    List<Submission> findByCourseId(@Param("courseId") Long courseId);
} 