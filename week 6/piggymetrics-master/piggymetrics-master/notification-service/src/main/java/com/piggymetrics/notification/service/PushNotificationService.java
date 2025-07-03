package com.piggymetrics.notification.service;

import reactor.core.publisher.Mono;

public interface PushNotificationService {
    Mono<Void> sendPush(String deviceToken, String title, String body, String dataJson);
} 