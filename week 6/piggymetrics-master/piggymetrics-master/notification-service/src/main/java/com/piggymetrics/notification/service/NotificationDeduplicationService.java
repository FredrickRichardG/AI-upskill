package com.piggymetrics.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class NotificationDeduplicationService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final String dedupPrefix;
    private final long dedupTtlSeconds;

    public NotificationDeduplicationService(
            ReactiveRedisTemplate<String, String> redisTemplate,
            @Value("${notification.dedup-prefix:notif:}") String dedupPrefix,
            @Value("${notification.dedup-ttl-seconds:86400}") long dedupTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.dedupPrefix = dedupPrefix;
        this.dedupTtlSeconds = dedupTtlSeconds;
    }

    public Mono<Boolean> isDuplicate(String hash) {
        String key = dedupPrefix + hash;
        return redisTemplate.opsForValue().get(key)
                .map(val -> true)
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> markSent(String hash) {
        String key = dedupPrefix + hash;
        return redisTemplate.opsForValue()
                .set(key, "sent", Duration.ofSeconds(dedupTtlSeconds));
    }
} 