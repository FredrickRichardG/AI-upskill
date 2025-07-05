package com.lms.courseservice.controller;

import com.lms.courseservice.dto.CategoryDto;
import com.lms.courseservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    
    private final CategoryService categoryService;
    
    // Category CRUD Operations
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto request) {
        log.info("Creating category: {}", request.getName());
        CategoryDto category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, 
                                                   @RequestBody CategoryDto request) {
        log.info("Updating category: {}", categoryId);
        CategoryDto category = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryId) {
        log.info("Getting category by ID: {}", categoryId);
        CategoryDto category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDto> getCategoryBySlug(@PathVariable String slug) {
        log.info("Getting category by slug: {}", slug);
        CategoryDto category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        log.info("Getting all categories");
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/paged")
    public ResponseEntity<Page<CategoryDto>> getAllCategoriesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting all categories - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDto> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.info("Deleting category: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
    
    // Category Search
    @GetMapping("/search")
    public ResponseEntity<List<CategoryDto>> searchCategories(@RequestParam String keyword) {
        log.info("Searching categories with keyword: {}", keyword);
        List<CategoryDto> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }
    
    // Category Statistics
    @GetMapping("/with-course-count")
    public ResponseEntity<List<CategoryDto>> getCategoriesWithCourseCount() {
        log.info("Getting categories with course count");
        List<CategoryDto> categories = categoryService.getCategoriesWithCourseCount();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/with-published-courses")
    public ResponseEntity<List<CategoryDto>> getCategoriesWithPublishedCourses() {
        log.info("Getting categories with published courses");
        List<CategoryDto> categories = categoryService.getCategoriesWithPublishedCourses();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CategoryDto>> getActiveCategories() {
        log.info("Getting active categories");
        List<CategoryDto> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
    
    // Category Statistics
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getCategoryCount() {
        log.info("Getting category count");
        Long count = categoryService.getCategoryCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveCategoryCount() {
        log.info("Getting active category count");
        Long count = categoryService.getActiveCategoryCount();
        return ResponseEntity.ok(count);
    }
    
    // Health Check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Category Service is running");
    }
} 