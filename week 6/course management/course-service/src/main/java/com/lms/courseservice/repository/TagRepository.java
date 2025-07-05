package com.lms.courseservice.repository;

import com.lms.courseservice.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // Basic CRUD operations
    Optional<Tag> findBySlug(String slug);
    Optional<Tag> findByName(String name);
    
    // Search tags by name
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Tag> searchByName(@Param("keyword") String keyword);
    
    // Find tags with course count
    @Query("SELECT t, COUNT(c) as courseCount FROM Tag t " +
           "LEFT JOIN t.courses c " +
           "GROUP BY t " +
           "ORDER BY courseCount DESC")
    List<Object[]> findTagsWithCourseCount();
    
    // Find tags with minimum course count
    @Query("SELECT t FROM Tag t " +
           "WHERE (SELECT COUNT(c) FROM t.courses c) >= :minCourses")
    List<Tag> findByMinCourseCount(@Param("minCourses") long minCourses);
    
    // Find tags by course count range
    @Query("SELECT t FROM Tag t " +
           "WHERE (SELECT COUNT(c) FROM t.courses c) BETWEEN :minCourses AND :maxCourses")
    List<Tag> findByCourseCountBetween(@Param("minCourses") long minCourses, 
                                     @Param("maxCourses") long maxCourses);
    
    // Find tags with published courses only
    @Query("SELECT DISTINCT t FROM Tag t " +
           "JOIN t.courses c " +
           "WHERE c.status = 'PUBLISHED'")
    List<Tag> findTagsWithPublishedCourses();
    
    // Find tags by creation date range
    @Query("SELECT t FROM Tag t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Tag> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                   @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find tags by color
    List<Tag> findByColor(String color);
    
    // Find popular tags (most used)
    @Query("SELECT t FROM Tag t " +
           "WHERE (SELECT COUNT(c) FROM t.courses c) > 0 " +
           "ORDER BY (SELECT COUNT(c) FROM t.courses c) DESC")
    List<Tag> findPopularTags();
    
    // Find tags ordered by name
    List<Tag> findAllByOrderByNameAsc();
    
    // Find tags with pagination
    Page<Tag> findAll(Pageable pageable);
    
    // Count tags
    long count();
} 