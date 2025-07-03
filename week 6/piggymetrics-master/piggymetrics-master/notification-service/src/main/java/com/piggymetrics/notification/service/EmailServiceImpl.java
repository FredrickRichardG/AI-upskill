package com.piggymetrics.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.domain.OutboxEvent;
import com.piggymetrics.notification.events.NotificationSentEvent;
import com.piggymetrics.notification.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

@Service
@RefreshScope
public class EmailServiceImpl implements EmailService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Environment env;

	@Autowired
	private NotificationDeduplicationService deduplicationService;

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Mono<Void> send(NotificationType type, Recipient recipient, String attachment) {
		String dedupHash = Objects.hash(type, recipient.getEmail(), attachment) + "";
		return deduplicationService.isDuplicate(dedupHash)
				.flatMap(isDup -> {
					if (isDup) {
						log.info("Duplicate email notification detected for {}", recipient.getEmail());
						return Mono.empty();
					}
					return Mono.fromRunnable(() -> {
						try {
							final String subject = env.getProperty(type.getSubject());
							final String text = MessageFormat.format(env.getProperty(type.getText()), recipient.getAccountName());

							MimeMessage message = mailSender.createMimeMessage();
							MimeMessageHelper helper = new MimeMessageHelper(message, true);
							helper.setTo(recipient.getEmail());
							helper.setSubject(subject);
							helper.setText(text);

							if (StringUtils.hasLength(attachment)) {
								helper.addAttachment(env.getProperty(type.getAttachment()), new ByteArrayResource(attachment.getBytes()));
							}

							mailSender.send(message);
							log.info("{} email notification has been sent to {}", type, recipient.getEmail());

							// Write to outbox
							NotificationSentEvent event = new NotificationSentEvent(recipient.getEmail(), "EMAIL", "SENT", subject);
							OutboxEvent outbox = new OutboxEvent();
							outbox.setEventType("notification.sent");
							outbox.setPayload(objectMapper.writeValueAsString(event));
							outboxEventRepository.save(outbox);
						} catch (Exception e) {
							throw new RuntimeException("Failed to send email notification", e);
						}
					}).then(deduplicationService.markSent(dedupHash)).then();
				});
	}
}
