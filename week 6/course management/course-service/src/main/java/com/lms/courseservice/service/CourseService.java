package com.lms.courseservice.service;

import com.lms.courseservice.dto.CourseCreateRequest;
import com.lms.courseservice.dto.CourseDto;
import com.lms.courseservice.dto.CourseSearchRequest;
import com.lms.courseservice.dto.CategoryDto;
import com.lms.courseservice.dto.TagDto;
import com.lms.courseservice.dto.InstructorDto;
import com.lms.courseservice.dto.ModuleDto;
import com.lms.courseservice.dto.ContentVersionDto;
import com.lms.courseservice.model.*;
import com.lms.courseservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final InstructorRepository instructorRepository;
    private final CourseAssignmentRepository assignmentRepository;
    private final ContentVersionRepository versionRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final ResourceRepository resourceRepository;
    private final WebClient.Builder webClientBuilder;
    
    // Course CRUD Operations
    public CourseDto createCourse(CourseCreateRequest request) {
        log.info("Creating new course: {}", request.getTitle());
        
        // Validate category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Create course
        Course course = Course.builder()
                .title(request.getTitle())
                .slug(generateSlug(request.getTitle()))
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .price(request.getPrice())
                .difficulty(request.getDifficulty())
                .duration(request.getDuration())
                .thumbnailUrl(request.getThumbnailUrl())
                .previewVideoUrl(request.getPreviewVideoUrl())
                .status(CourseStatus.DRAFT)
                .isFeatured(request.getIsFeatured())
                .isPublic(request.getIsPublic())
                .maxStudents(request.getMaxStudents())
                .prerequisites(request.getPrerequisites())
                .learningObjectives(request.getLearningObjectives())
                .targetAudience(request.getTargetAudience())
                .certificateTemplate(request.getCertificateTemplate())
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Set tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = tagRepository.findAllById(request.getTagIds()).stream()
                    .collect(Collectors.toSet());
            course.setTags(tags);
        }
        
        Course savedCourse = courseRepository.save(course);
        
        // Create instructor assignments
        if (request.getInstructorIds() != null && !request.getInstructorIds().isEmpty()) {
            List<Instructor> instructors = instructorRepository.findAllById(request.getInstructorIds());
            for (Instructor instructor : instructors) {
                CourseAssignment assignment = CourseAssignment.builder()
                        .course(savedCourse)
                        .instructor(instructor)
                        .role(AssignmentRole.INSTRUCTOR)
                        .assignedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                assignmentRepository.save(assignment);
            }
        }
        
        // Create initial content version
        ContentVersion version = ContentVersion.builder()
                .course(savedCourse)
                .version("1.0.0")
                .changeLog("Initial version")
                .status(VersionStatus.DRAFT)
                .createdBy("system")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        versionRepository.save(version);
        
        return mapToDto(savedCourse);
    }
    
    public CourseDto updateCourse(Long courseId, CourseCreateRequest request) {
        log.info("Updating course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Update basic fields
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setPrice(request.getPrice());
        course.setDifficulty(request.getDifficulty());
        course.setDuration(request.getDuration());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPreviewVideoUrl(request.getPreviewVideoUrl());
        course.setIsFeatured(request.getIsFeatured());
        course.setIsPublic(request.getIsPublic());
        course.setMaxStudents(request.getMaxStudents());
        course.setPrerequisites(request.getPrerequisites());
        course.setLearningObjectives(request.getLearningObjectives());
        course.setTargetAudience(request.getTargetAudience());
        course.setCertificateTemplate(request.getCertificateTemplate());
        course.setUpdatedAt(LocalDateTime.now());
        
        // Update category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            course.setCategory(category);
        }
        
        // Update tags
        if (request.getTagIds() != null) {
            Set<Tag> tags = tagRepository.findAllById(request.getTagIds()).stream()
                    .collect(Collectors.toSet());
            course.setTags(tags);
        }
        
        Course savedCourse = courseRepository.save(course);
        
        // Update instructor assignments
        if (request.getInstructorIds() != null) {
            // Remove existing assignments
            assignmentRepository.deleteByCourseId(courseId);
            
            // Create new assignments
            List<Instructor> instructors = instructorRepository.findAllById(request.getInstructorIds());
            for (Instructor instructor : instructors) {
                CourseAssignment assignment = CourseAssignment.builder()
                        .course(savedCourse)
                        .instructor(instructor)
                        .role(AssignmentRole.INSTRUCTOR)
                        .assignedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                assignmentRepository.save(assignment);
            }
        }
        
        return mapToDto(savedCourse);
    }
    
    public CourseDto getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToDto(course);
    }
    
    public CourseDto getCourseBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToDto(course);
    }
    
    public Page<CourseDto> getAllCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(pageable);
        return courses.map(this::mapToDto);
    }
    
    public void deleteCourse(Long courseId) {
        log.info("Deleting course: {}", courseId);
        courseRepository.deleteById(courseId);
    }
    
    // Course Search and Filtering
    public Page<CourseDto> searchCourses(CourseSearchRequest request) {
        Page<Course> courses = courseRepository.searchCourses(
                request.getKeyword(),
                request.getCategoryId(),
                request.getStatus(),
                request.getDifficulty(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getPageable()
        );
        return courses.map(this::mapToDto);
    }
    
    public List<CourseDto> getCoursesByCategory(Long categoryId) {
        List<Course> courses = courseRepository.findByCategoryId(categoryId);
        return courses.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<CourseDto> getCoursesByInstructor(Long instructorId) {
        List<Course> courses = courseRepository.findByInstructorId(instructorId);
        return courses.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<CourseDto> getPublishedCourses() {
        List<Course> courses = courseRepository.findByStatusOrderByCreatedAtDesc(CourseStatus.PUBLISHED);
        return courses.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<CourseDto> getFeaturedCourses() {
        List<Course> courses = courseRepository.findByStatusAndIsFeaturedTrue(CourseStatus.PUBLISHED);
        return courses.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Course Status Management
    public CourseDto publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        Course savedCourse = courseRepository.save(course);
        return mapToDto(savedCourse);
    }
    
    public CourseDto unpublishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setStatus(CourseStatus.DRAFT);
        course.setUpdatedAt(LocalDateTime.now());
        
        Course savedCourse = courseRepository.save(course);
        return mapToDto(savedCourse);
    }
    
    // Course Statistics
    public Long getCourseCount() {
        return courseRepository.count();
    }
    
    public Long getPublishedCourseCount() {
        return courseRepository.countByStatus(CourseStatus.PUBLISHED);
    }
    
    // Helper Methods
    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
    
    private CourseDto mapToDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .shortDescription(course.getShortDescription())
                .price(course.getPrice())
                .difficulty(course.getDifficulty())
                .duration(course.getDuration())
                .thumbnailUrl(course.getThumbnailUrl())
                .previewVideoUrl(course.getPreviewVideoUrl())
                .status(course.getStatus())
                .isFeatured(course.getIsFeatured())
                .isPublic(course.getIsPublic())
                .maxStudents(course.getMaxStudents())
                .prerequisites(course.getPrerequisites())
                .learningObjectives(course.getLearningObjectives())
                .targetAudience(course.getTargetAudience())
                .certificateTemplate(course.getCertificateTemplate())
                .publishedAt(course.getPublishedAt())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .category(mapCategoryToDto(course.getCategory()))
                .tags(course.getTags().stream().map(this::mapTagToDto).collect(Collectors.toSet()))
                .instructors(getInstructorsForCourse(course.getId()))
                .modules(getModulesForCourse(course.getId()))
                .latestVersion(getLatestVersionForCourse(course.getId()))
                .totalLessons(getTotalLessonsForCourse(course.getId()))
                .totalModules(getTotalModulesForCourse(course.getId()))
                .totalResources(getTotalResourcesForCourse(course.getId()))
                .build();
    }
    
    private CategoryDto mapCategoryToDto(Category category) {
        if (category == null) return null;
        
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
                .build();
    }
    
    private TagDto mapTagToDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .description(tag.getDescription())
                .color(tag.getColor())
                .isActive(tag.getIsActive())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
    
    private List<InstructorDto> getInstructorsForCourse(Long courseId) {
        List<CourseAssignment> assignments = assignmentRepository.findByCourseId(courseId);
        return assignments.stream()
                .map(assignment -> mapInstructorToDto(assignment.getInstructor()))
                .collect(Collectors.toList());
    }
    
    private InstructorDto mapInstructorToDto(Instructor instructor) {
        return InstructorDto.builder()
                .id(instructor.getId())
                .firstName(instructor.getFirstName())
                .lastName(instructor.getLastName())
                .email(instructor.getEmail())
                .bio(instructor.getBio())
                .profileImageUrl(instructor.getProfileImageUrl())
                .expertise(instructor.getExpertise())
                .qualification(instructor.getQualification())
                .yearsOfExperience(instructor.getYearsOfExperience())
                .linkedinUrl(instructor.getLinkedinUrl())
                .twitterUrl(instructor.getTwitterUrl())
                .websiteUrl(instructor.getWebsiteUrl())
                .isActive(instructor.getIsActive())
                .createdAt(instructor.getCreatedAt())
                .updatedAt(instructor.getUpdatedAt())
                .build();
    }
    
    private List<ModuleDto> getModulesForCourse(Long courseId) {
        List<com.lms.courseservice.model.Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return modules.stream().map(this::mapModuleToDto).collect(Collectors.toList());
    }
    
    private ModuleDto mapModuleToDto(com.lms.courseservice.model.Module module) {
        return ModuleDto.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .isPublished(module.getIsPublished())
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .courseId(module.getCourse().getId())
                .lessonCount(lessonRepository.countByModuleId(module.getId()))
                .resourceCount(resourceRepository.countByModuleId(module.getId()))
                .build();
    }
    
    private ContentVersionDto getLatestVersionForCourse(Long courseId) {
        List<ContentVersion> versions = versionRepository.findLatestVersionsByCourse(courseId);
        if (versions.isEmpty()) return null;
        
        ContentVersion latestVersion = versions.get(0);
        return mapContentVersionToDto(latestVersion);
    }
    
    private ContentVersionDto mapContentVersionToDto(ContentVersion version) {
        return ContentVersionDto.builder()
                .id(version.getId())
                .version(version.getVersion())
                .changeLog(version.getChangeLog())
                .status(version.getStatus())
                .createdBy(version.getCreatedBy())
                .reviewedBy(version.getReviewedBy())
                .reviewedAt(version.getReviewedAt())
                .comments(version.getComments())
                .createdAt(version.getCreatedAt())
                .updatedAt(version.getUpdatedAt())
                .courseId(version.getCourse().getId())
                .build();
    }
    
    private Long getTotalLessonsForCourse(Long courseId) {
        return lessonRepository.countByCourseId(courseId);
    }
    
    private Long getTotalModulesForCourse(Long courseId) {
        return moduleRepository.countByCourseId(courseId);
    }
    
    private Long getTotalResourcesForCourse(Long courseId) {
        return resourceRepository.countByCourseId(courseId);
    }

    @CircuitBreaker(name = "courseServiceCB", fallbackMethod = "fallbackGetUser")
    public Mono<String> getUserById(Long userId) {
        WebClient webClient = webClientBuilder.baseUrl("http://user-service:8081").build();
        return webClient.get()
                .uri("/users/" + userId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fallbackGetUser(Long userId, Throwable t) {
        log.warn("Fallback triggered for getUserById: {}", t.getMessage());
        return Mono.just("Dummy user response due to service unavailability");
    }
} 