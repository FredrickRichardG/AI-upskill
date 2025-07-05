package com.lms.courseservice.repository;

import com.lms.courseservice.model.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    // Basic CRUD operations
    List<Module> findByCourseId(Long courseId);
    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    Optional<Module> findByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);
    
    // Search modules by title and description
    @Query("SELECT m FROM Module m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Module> searchByKeyword(@Param("keyword") String keyword);
    
    // Search modules by course and keyword
    @Query("SELECT m FROM Module m " +
           "WHERE m.course.id = :courseId AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Module> searchByCourseAndKeyword(@Param("courseId") Long courseId,
                                        @Param("keyword") String keyword);
    
    // Find modules by creation date range
    @Query("SELECT m FROM Module m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    List<Module> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find modules by course and creation date range
    @Query("SELECT m FROM Module m " +
           "WHERE m.course.id = :courseId AND m.createdAt BETWEEN :startDate AND :endDate")
    List<Module> findByCourseAndCreatedAtBetween(@Param("courseId") Long courseId,
                                               @Param("startDate") java.time.LocalDateTime startDate,
                                               @Param("endDate") java.time.LocalDateTime endDate);
    
    // Find modules with minimum lessons
    @Query("SELECT m FROM Module m " +
           "WHERE (SELECT COUNT(l) FROM m.lessons l) >= :minLessons")
    List<Module> findByMinLessons(@Param("minLessons") long minLessons);
    
    // Find modules with maximum lessons
    @Query("SELECT m FROM Module m " +
           "WHERE (SELECT COUNT(l) FROM m.lessons l) <= :maxLessons")
    List<Module> findByMaxLessons(@Param("maxLessons") long maxLessons);
    
    // Find modules by lesson count range
    @Query("SELECT m FROM Module m " +
           "WHERE (SELECT COUNT(l) FROM m.lessons l) BETWEEN :minLessons AND :maxLessons")
    List<Module> findByLessonCountBetween(@Param("minLessons") long minLessons,
                                        @Param("maxLessons") long maxLessons);
    
    // Find modules with resources
    @Query("SELECT m FROM Module m " +
           "WHERE (SELECT COUNT(r) FROM m.resources r) > 0")
    List<Module> findModulesWithResources();
    
    // Find modules by resource count
    @Query("SELECT m FROM Module m " +
           "WHERE (SELECT COUNT(r) FROM m.resources r) >= :minResources")
    List<Module> findByMinResources(@Param("minResources") long minResources);
    
    // Count modules by course
    long countByCourseId(Long courseId);
    
    // Find modules with pagination
    Page<Module> findAll(Pageable pageable);
    
    // Find modules by course with pagination
    Page<Module> findByCourseId(Long courseId, Pageable pageable);
    
    // Find modules ordered by creation date
    List<Module> findAllByOrderByCreatedAtDesc();
    
    // Find modules by course ordered by creation date
    List<Module> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    
    // Find modules by order index range
    List<Module> findByOrderIndexBetween(Integer minOrder, Integer maxOrder);
    List<Module> findByCourseIdAndOrderIndexBetween(Long courseId, Integer minOrder, Integer maxOrder);
    
    // Find next module in sequence
    @Query("SELECT m FROM Module m " +
           "WHERE m.course.id = :courseId AND m.orderIndex > :currentOrder " +
           "ORDER BY m.orderIndex ASC")
    List<Module> findNextModules(@Param("courseId") Long courseId, @Param("currentOrder") Integer currentOrder);
    
    // Find previous module in sequence
    @Query("SELECT m FROM Module m " +
           "WHERE m.course.id = :courseId AND m.orderIndex < :currentOrder " +
           "ORDER BY m.orderIndex DESC")
    List<Module> findPreviousModules(@Param("courseId") Long courseId, @Param("currentOrder") Integer currentOrder);
} 