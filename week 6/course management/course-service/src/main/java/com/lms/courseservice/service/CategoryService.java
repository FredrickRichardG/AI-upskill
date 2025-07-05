package com.lms.courseservice.service;

import com.lms.courseservice.dto.CategoryDto;
import com.lms.courseservice.model.Category;
import com.lms.courseservice.repository.CategoryRepository;
import com.lms.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    
    // Category CRUD Operations
    public CategoryDto createCategory(CategoryDto request) {
        log.info("Creating new category: {}", request.getName());
        
        Category category = Category.builder()
                .name(request.getName())
                .slug(generateSlug(request.getName()))
                .description(request.getDescription())
                .color(request.getColor())
                .icon(request.getIcon())
                .isActive(true)
                .orderIndex(request.getOrderIndex())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }
    
    public CategoryDto updateCategory(Long categoryId, CategoryDto request) {
        log.info("Updating category: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setIsActive(request.getIsActive());
        category.setOrderIndex(request.getOrderIndex());
        category.setUpdatedAt(LocalDateTime.now());
        
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }
    
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToDto(category);
    }
    
    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToDto(category);
    }
    
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        return categories.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(this::mapToDto);
    }
    
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category: {}", categoryId);
        
        // Check if category has courses
        long courseCount = courseRepository.countByCategoryId(categoryId);
        if (courseCount > 0) {
            throw new RuntimeException("Cannot delete category with existing courses");
        }
        
        categoryRepository.deleteById(categoryId);
    }
    
    // Category Search
    public List<CategoryDto> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.searchByName(keyword);
        return categories.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Category Statistics
    public List<CategoryDto> getCategoriesWithCourseCount() {
        List<Object[]> results = categoryRepository.findCategoriesWithCourseCount();
        return results.stream()
                .map(result -> {
                    Category category = (Category) result[0];
                    Long courseCount = (Long) result[1];
                    CategoryDto dto = mapToDto(category);
                    dto.setCourseCount(courseCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getCategoriesWithPublishedCourses() {
        List<Category> categories = categoryRepository.findCategoriesWithPublishedCourses();
        return categories.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<CategoryDto> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Category Statistics
    public Long getCategoryCount() {
        return categoryRepository.count();
    }
    
    public Long getActiveCategoryCount() {
        return categoryRepository.countByIsActiveTrue();
    }
    
    // Helper Methods
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
    
    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .color(category.getColor())
                .icon(category.getIcon())
                .isActive(category.getIsActive())
                .orderIndex(category.getOrderIndex())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .courseCount(courseRepository.countByCategoryId(category.getId()))
                .publishedCourseCount(courseRepository.countByCategoryIdAndStatus(category.getId(), 
                        com.lms.courseservice.model.CourseStatus.PUBLISHED))
                .build();
    }
} 