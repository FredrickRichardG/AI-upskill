package com.piggymetrics.course.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.course.domain.Course;
import com.piggymetrics.course.domain.OutboxEvent;
import com.piggymetrics.course.events.CourseCreatedEvent;
import com.piggymetrics.course.repository.CourseRepository;
import com.piggymetrics.course.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Flux<Course> findAll() {
        return Mono.fromCallable(() -> courseRepository.findAll())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Course> findById(Long id) {
        return Mono.fromCallable(() -> courseRepository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Course> create(Course course) {
        return Mono.fromCallable(() -> {
            Course saved = courseRepository.save(course);
            // Write to outbox
            CourseCreatedEvent event = new CourseCreatedEvent(saved.getId(), saved.getTitle(), saved.getOwnerId());
            OutboxEvent outbox = new OutboxEvent();
            outbox.setEventType("course.created");
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outboxEventRepository.save(outbox);
            return saved;
        }).subscribeOn(Schedulers.boundedElastic());
    }
} 