package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    @Autowired
    private EmailService emailService;
    @Autowired
    private PushNotificationService pushNotificationService;

    @RabbitListener(queues = "#{'${notification.queue}'}")
    public void onNotificationEvent(NotificationEvent event) {
        if (event.getChannel() == NotificationEvent.Channel.EMAIL) {
            try {
                emailService.send(null, new com.piggymetrics.notification.domain.Recipient() {{ setEmail(event.getRecipient()); }}, event.getAttachment())
                    .doOnError(e -> log.error("Failed to send email notification", e))
                    .subscribe();
            } catch (Exception e) {
                log.error("Exception in email notification", e);
            }
        } else if (event.getChannel() == NotificationEvent.Channel.PUSH) {
            pushNotificationService.sendPush(event.getRecipient(), event.getSubject(), event.getBody(), event.getDataJson())
                .doOnError(e -> log.error("Failed to send push notification", e))
                .subscribe();
        }
    }
} 