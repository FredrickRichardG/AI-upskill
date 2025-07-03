package com.piggymetrics.course.service;

import com.piggymetrics.course.domain.Course;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseService {
    Flux<Course> findAll();
    Mono<Course> findById(Long id);
    Mono<Course> create(Course course);
} 