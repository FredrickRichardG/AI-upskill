package com.lms.courseservice.repository;

import com.lms.courseservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Basic CRUD operations
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByName(String name);
    
    // Search categories by name
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByName(@Param("keyword") String keyword);
    
    // Find categories with course count
    @Query("SELECT c, COUNT(cr) as courseCount FROM Category c " +
           "LEFT JOIN c.courses cr " +
           "GROUP BY c " +
           "ORDER BY courseCount DESC")
    List<Object[]> findCategoriesWithCourseCount();
    
    // Find categories with minimum course count
    @Query("SELECT c FROM Category c " +
           "WHERE (SELECT COUNT(cr) FROM c.courses cr) >= :minCourses")
    List<Category> findByMinCourseCount(@Param("minCourses") long minCourses);
    
    // Find categories by course count range
    @Query("SELECT c FROM Category c " +
           "WHERE (SELECT COUNT(cr) FROM c.courses cr) BETWEEN :minCourses AND :maxCourses")
    List<Category> findByCourseCountBetween(@Param("minCourses") long minCourses, 
                                          @Param("maxCourses") long maxCourses);
    
    // Find categories with published courses only
    @Query("SELECT DISTINCT c FROM Category c " +
           "JOIN c.courses cr " +
           "WHERE cr.status = 'PUBLISHED'")
    List<Category> findCategoriesWithPublishedCourses();
    
    // Find categories by creation date range
    @Query("SELECT c FROM Category c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Category> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                        @Param("endDate") java.time.LocalDateTime endDate);
    
    // Count categories
    long count();
    
    // Find categories ordered by name
    List<Category> findAllByOrderByNameAsc();
    
    // Find categories with pagination
    Page<Category> findAll(Pageable pageable);
    
    // Find active categories
    List<Category> findByIsActiveTrue();
    
    // Count active categories
    long countByIsActiveTrue();
} 