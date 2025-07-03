package com.piggymetrics.notification.repository;

import com.piggymetrics.notification.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
} 