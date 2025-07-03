package com.piggymetrics.enrollment.repository;

import com.piggymetrics.enrollment.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
} 