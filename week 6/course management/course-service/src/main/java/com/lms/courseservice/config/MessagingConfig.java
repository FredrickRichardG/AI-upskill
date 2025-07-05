package com.lms.courseservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class MessagingConfig {

    // RabbitMQ Configuration
    public static final String CHAT_QUEUE = "chat.queue";
    public static final String CHAT_EXCHANGE = "chat.exchange";
    public static final String CHAT_ROUTING_KEY = "chat.message";
    
    public static final String ANNOUNCEMENT_QUEUE = "announcement.queue";
    public static final String ANNOUNCEMENT_EXCHANGE = "announcement.exchange";
    public static final String ANNOUNCEMENT_ROUTING_KEY = "announcement.message";
    
    public static final String QUIZ_QUEUE = "quiz.queue";
    public static final String QUIZ_EXCHANGE = "quiz.exchange";
    public static final String QUIZ_ROUTING_KEY = "quiz.message";
    
    public static final String PROGRESS_QUEUE = "progress.queue";
    public static final String PROGRESS_EXCHANGE = "progress.exchange";
    public static final String PROGRESS_ROUTING_KEY = "progress.message";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Chat messaging
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE, true);
    }

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE);
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(CHAT_ROUTING_KEY);
    }

    // Announcement messaging
    @Bean
    public Queue announcementQueue() {
        return new Queue(ANNOUNCEMENT_QUEUE, true);
    }

    @Bean
    public TopicExchange announcementExchange() {
        return new TopicExchange(ANNOUNCEMENT_EXCHANGE);
    }

    @Bean
    public Binding announcementBinding(Queue announcementQueue, TopicExchange announcementExchange) {
        return BindingBuilder.bind(announcementQueue).to(announcementExchange).with(ANNOUNCEMENT_ROUTING_KEY);
    }

    // Quiz messaging
    @Bean
    public Queue quizQueue() {
        return new Queue(QUIZ_QUEUE, true);
    }

    @Bean
    public TopicExchange quizExchange() {
        return new TopicExchange(QUIZ_EXCHANGE);
    }

    @Bean
    public Binding quizBinding(Queue quizQueue, TopicExchange quizExchange) {
        return BindingBuilder.bind(quizQueue).to(quizExchange).with(QUIZ_ROUTING_KEY);
    }

    // Progress messaging
    @Bean
    public Queue progressQueue() {
        return new Queue(PROGRESS_QUEUE, true);
    }

    @Bean
    public TopicExchange progressExchange() {
        return new TopicExchange(PROGRESS_EXCHANGE);
    }

    @Bean
    public Binding progressBinding(Queue progressQueue, TopicExchange progressExchange) {
        return BindingBuilder.bind(progressQueue).to(progressExchange).with(PROGRESS_ROUTING_KEY);
    }

    // Redis Configuration
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
} 