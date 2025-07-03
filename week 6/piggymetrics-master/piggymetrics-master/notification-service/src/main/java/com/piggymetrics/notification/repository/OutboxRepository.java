package com.piggymetrics.notification.repository;

import com.piggymetrics.notification.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
} 