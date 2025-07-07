package com.lms.notificationservice.controller;

import com.lms.notificationservice.model.Outbox;
import com.lms.notificationservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final OutboxRepository outboxRepository;

    @PostMapping("/email")
    @CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "sendEmailFallback")
    public ResponseEntity<Outbox> sendEmail(@RequestBody Outbox outbox) {
        outbox.setChannel("EMAIL");
        Outbox saved = outboxRepository.save(outbox);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<Outbox> sendEmailFallback(Outbox outbox, Throwable t) {
        return ResponseEntity.status(503).build();
    }

    @PostMapping("/schedule")
    public ResponseEntity<Outbox> scheduleNotification(@RequestBody Outbox outbox) {
        Outbox saved = outboxRepository.save(outbox);
        return ResponseEntity.ok(saved);
    }
} 