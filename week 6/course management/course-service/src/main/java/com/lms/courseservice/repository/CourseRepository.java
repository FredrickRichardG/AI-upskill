package com.lms.courseservice.repository;

import com.lms.courseservice.model.Course;
import com.lms.courseservice.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    // Basic CRUD operations
    Optional<Course> findBySlug(String slug);
    List<Course> findByStatus(CourseStatus status);
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    
    // Search by title and description
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Filter by category
    List<Course> findByCategoryId(Long categoryId);
    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Filter by instructor
    @Query("SELECT DISTINCT c FROM Course c " +
           "JOIN c.assignments ca " +
           "WHERE ca.instructor.id = :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") Long instructorId);
    
    // Filter by price range
    List<Course> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    Page<Course> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Filter by difficulty
    List<Course> findByDifficulty(String difficulty);
    Page<Course> findByDifficulty(String difficulty, Pageable pageable);
    
    // Filter by duration range
    List<Course> findByDurationBetween(Integer minDuration, Integer maxDuration);
    Page<Course> findByDurationBetween(Integer minDuration, Integer maxDuration, Pageable pageable);
    
    // Filter by tags
    @Query("SELECT DISTINCT c FROM Course c " +
           "JOIN c.tags t " +
           "WHERE t.id IN :tagIds")
    List<Course> findByTagIds(@Param("tagIds") List<Long> tagIds);
    
    // Advanced search with multiple criteria
    @Query("SELECT c FROM Course c " +
           "WHERE (:keyword IS NULL OR (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "AND (:categoryId IS NULL OR c.category.id = :categoryId) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:difficulty IS NULL OR c.difficulty = :difficulty) " +
           "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR c.price <= :maxPrice)")
    Page<Course> searchCourses(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("status") CourseStatus status,
        @Param("difficulty") String difficulty,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    // Find published courses
    List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);
    Page<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status, Pageable pageable);
    
    // Find featured courses
    List<Course> findByStatusAndIsFeaturedTrue(CourseStatus status);
    
    // Find courses by creation date range
    @Query("SELECT c FROM Course c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Course> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                       @Param("endDate") java.time.LocalDateTime endDate);
    
    // Count courses by status
    long countByStatus(CourseStatus status);
    
    // Count courses by category
    long countByCategoryId(Long categoryId);
    
    // Count courses by category and status
    long countByCategoryIdAndStatus(Long categoryId, CourseStatus status);
    
    // Find courses with modules count
    @Query("SELECT c, COUNT(m) as moduleCount FROM Course c " +
           "LEFT JOIN c.modules m " +
           "GROUP BY c " +
           "HAVING COUNT(m) >= :minModules")
    List<Course> findByMinModules(@Param("minModules") long minModules);
} 