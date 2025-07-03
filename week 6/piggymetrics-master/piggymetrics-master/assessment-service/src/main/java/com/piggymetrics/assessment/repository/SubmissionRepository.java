package com.piggymetrics.assessment.repository;

import com.piggymetrics.assessment.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
} 