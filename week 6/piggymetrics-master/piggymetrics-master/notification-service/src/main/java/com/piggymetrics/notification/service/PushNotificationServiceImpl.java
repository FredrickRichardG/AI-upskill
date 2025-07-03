package com.piggymetrics.notification.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.notification.domain.OutboxEvent;
import com.piggymetrics.notification.events.NotificationSentEvent;
import com.piggymetrics.notification.repository.OutboxEventRepository;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {

    @Value("${fcm.service-account-file}")
    private Resource serviceAccount;

    private boolean initialized = false;

    @Autowired
    private NotificationDeduplicationService deduplicationService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        if (!initialized) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            initialized = true;
        }
    }

    @Override
    public Mono<Void> sendPush(String deviceToken, String title, String body, String dataJson) {
        String dedupHash = Objects.hash(deviceToken, title, body, dataJson) + "";
        return deduplicationService.isDuplicate(dedupHash)
                .flatMap(isDup -> {
                    if (isDup) {
                        return Mono.empty();
                    }
                    return Mono.fromRunnable(() -> {
                        try {
                            Message.Builder builder = Message.builder()
                                    .setToken(deviceToken)
                                    .setNotification(Notification.builder().setTitle(title).setBody(body).build());
                            if (dataJson != null && !dataJson.isEmpty()) {
                                builder.putData("payload", dataJson);
                            }
                            FirebaseMessaging.getInstance().send(builder.build());

                            // Write to outbox
                            NotificationSentEvent event = new NotificationSentEvent(deviceToken, "PUSH", "SENT", title);
                            OutboxEvent outbox = new OutboxEvent();
                            outbox.setEventType("notification.sent");
                            outbox.setPayload(objectMapper.writeValueAsString(event));
                            outboxEventRepository.save(outbox);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to send push notification", e);
                        }
                    }).then(deduplicationService.markSent(dedupHash)).then();
                });
    }
} 