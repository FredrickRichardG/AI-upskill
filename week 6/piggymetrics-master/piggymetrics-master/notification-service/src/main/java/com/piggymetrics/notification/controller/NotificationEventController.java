package com.piggymetrics.notification.controller;

import com.piggymetrics.notification.domain.NotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notifications")
public class NotificationEventController {

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public NotificationEventController(RabbitTemplate rabbitTemplate,
                                       @Value("${notification.queue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    @PostMapping("/event")
    public Mono<ResponseEntity<String>> sendNotificationEvent(@RequestBody NotificationEvent event) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(queueName, event))
                .thenReturn(ResponseEntity.ok("Notification event published"));
    }

    @PostMapping("/email")
    public Mono<ResponseEntity<String>> sendEmailNotification(@RequestBody NotificationEvent event) {
        event.setChannel(NotificationEvent.Channel.EMAIL);
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(queueName, event))
                .thenReturn(ResponseEntity.ok("Email notification event published"));
    }

    @PostMapping("/schedule")
    public Mono<ResponseEntity<String>> scheduleNotification(@RequestBody NotificationEvent event) {
        // For demo, treat as generic event scheduling
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(queueName, event))
                .thenReturn(ResponseEntity.ok("Notification scheduled"));
    }
} 