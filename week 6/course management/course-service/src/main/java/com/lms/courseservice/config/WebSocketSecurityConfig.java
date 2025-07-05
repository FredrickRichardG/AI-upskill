package com.lms.courseservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/topic/chat/**").authenticated()
                .simpDestMatchers("/topic/announcements/**").authenticated()
                .simpDestMatchers("/topic/quiz/**").authenticated()
                .simpDestMatchers("/topic/lesson-progress/**").authenticated()
                .simpDestMatchers("/topic/course-notifications/**").authenticated()
                .simpDestMatchers("/topic/participants/**").authenticated()
                .simpDestMatchers("/topic/quiz-results/**").authenticated()
                .simpDestMatchers("/user/**/queue/notifications").authenticated()
                .simpDestMatchers("/app/chat/**").authenticated()
                .simpDestMatchers("/app/quiz/**").authenticated()
                .simpDestMatchers("/app/announcement/**").authenticated()
                .simpDestMatchers("/app/lesson/**").authenticated()
                .simpDestMatchers("/app/notifications/**").authenticated()
                .simpDestMatchers("/app/course/**").authenticated()
                .simpDestMatchers("/app/participants/**").authenticated()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Disable CSRF for WebSocket endpoints
        return true;
    }
} 