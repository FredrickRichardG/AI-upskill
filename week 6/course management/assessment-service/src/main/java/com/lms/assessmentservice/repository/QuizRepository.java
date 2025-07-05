package com.lms.assessmentservice.repository;

import com.lms.assessmentservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    @Query("SELECT q FROM Quiz q WHERE q.courseId = :courseId")
    List<Quiz> findByCourseId(@Param("courseId") Long courseId);
} 