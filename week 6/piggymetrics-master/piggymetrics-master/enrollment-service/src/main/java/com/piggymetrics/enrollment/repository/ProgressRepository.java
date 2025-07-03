package com.piggymetrics.enrollment.repository;

import com.piggymetrics.enrollment.domain.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
} 