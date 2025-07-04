package com.lms.courseservice.controller;

import com.lms.courseservice.model.Course;
import com.lms.courseservice.model.Module;
import com.lms.courseservice.repository.CourseRepository;
import com.lms.courseservice.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course saved = courseRepository.save(course);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/{id}/outline")
    public ResponseEntity<List<Module>> getCourseOutline(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            List<Module> modules = moduleRepository.findAll().stream()
                .filter(m -> m.getCourse().getCourseId().equals(id))
                .toList();
            return ResponseEntity.ok(modules);
        }
        return ResponseEntity.notFound().build();
    }
} 