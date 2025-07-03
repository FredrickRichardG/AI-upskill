package com.piggymetrics.course.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.course.domain.OutboxEvent;
import com.piggymetrics.course.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchangeName;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                           RabbitTemplate rabbitTemplate,
                           ObjectMapper objectMapper,
                           @Value("${course.events.exchange:course.events}") String exchangeName) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.exchangeName = exchangeName;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findBySentFalse();
        for (OutboxEvent event : events) {
            try {
                String routingKey = event.getEventType();
                rabbitTemplate.convertAndSend(exchangeName, routingKey, event.getPayload());
                event.setSent(true);
                outboxEventRepository.save(event);
                log.info("Published event {} to {} with routingKey {}", event.getId(), exchangeName, routingKey);
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                // Optionally: move to DLQ or mark for retry
            }
        }
    }
} 