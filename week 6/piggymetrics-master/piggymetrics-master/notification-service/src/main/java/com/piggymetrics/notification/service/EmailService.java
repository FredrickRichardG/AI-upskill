package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {

	Mono<Void> send(NotificationType type, Recipient recipient, String attachment) throws MessagingException, IOException;

}
