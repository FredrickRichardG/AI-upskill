package com.lms.notificationservice.controller;

import com.lms.notificationservice.model.Outbox;
import com.lms.notificationservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final OutboxRepository outboxRepository;

    @PostMapping("/email")
    public ResponseEntity<Outbox> sendEmail(@RequestBody Outbox outbox) {
        outbox.setChannel("EMAIL");
        Outbox saved = outboxRepository.save(outbox);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/schedule")
    public ResponseEntity<Outbox> scheduleNotification(@RequestBody Outbox outbox) {
        Outbox saved = outboxRepository.save(outbox);
        return ResponseEntity.ok(saved);
    }
} 