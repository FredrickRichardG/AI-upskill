package com.piggymetrics.course.controller;

import com.piggymetrics.course.domain.Course;
import com.piggymetrics.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public Flux<Course> getCourses() {
        return courseService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Course>> getCourseById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/outline")
    public Mono<ResponseEntity<Course>> getCourseOutline(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ROLE_INSTRUCTOR')")
    public Mono<Course> createCourse(@RequestBody Course course, Principal principal) {
        // In a real app, you would associate the course with the authenticated user (principal)
        course.setOwnerId(principal.getName());
        return courseService.create(course);
    }
} 