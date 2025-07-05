package com.lms.courseservice.service;

import com.lms.courseservice.dto.ResourceDto;
import com.lms.courseservice.model.Resource;
import com.lms.courseservice.model.Course;
import com.lms.courseservice.model.Module;
import com.lms.courseservice.model.Lesson;
import com.lms.courseservice.repository.ResourceRepository;
import com.lms.courseservice.repository.CourseRepository;
import com.lms.courseservice.repository.ModuleRepository;
import com.lms.courseservice.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResourceService {
    
    private final ResourceRepository resourceRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    
    // Resource CRUD Operations
    public ResourceDto createResource(ResourceDto request) {
        log.info("Creating new resource: {}", request.getName());
        
        Resource resource = Resource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .fileUrl(request.getFileUrl())
                .fileType(request.getFileType())
                .mimeType(request.getMimeType())
                .fileSize(request.getFileSize())
                .isPublic(request.getIsPublic())
                .downloadToken(request.getDownloadToken())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Set relationships
        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            resource.setCourse(course);
        }
        
        if (request.getModuleId() != null) {
            Module module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found"));
            resource.setModule(module);
        }
        
        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            resource.setLesson(lesson);
        }
        
        Resource savedResource = resourceRepository.save(resource);
        return mapToDto(savedResource);
    }
    
    public ResourceDto updateResource(Long resourceId, ResourceDto request) {
        log.info("Updating resource: {}", resourceId);
        
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setFileUrl(request.getFileUrl());
        resource.setFileType(request.getFileType());
        resource.setMimeType(request.getMimeType());
        resource.setFileSize(request.getFileSize());
        resource.setIsPublic(request.getIsPublic());
        resource.setUpdatedAt(LocalDateTime.now());
        
        Resource savedResource = resourceRepository.save(resource);
        return mapToDto(savedResource);
    }
    
    public ResourceDto getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return mapToDto(resource);
    }
    
    public List<ResourceDto> getResourcesByCourse(Long courseId) {
        List<Resource> resources = resourceRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getResourcesByModule(Long moduleId) {
        List<Resource> resources = resourceRepository.findByModuleIdAndFileType(moduleId, null);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getResourcesByLesson(Long lessonId) {
        List<Resource> resources = resourceRepository.findByLessonIdAndFileType(lessonId, null);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public void deleteResource(Long resourceId) {
        log.info("Deleting resource: {}", resourceId);
        resourceRepository.deleteById(resourceId);
    }
    
    // Resource Search and Filtering
    public List<ResourceDto> searchResources(String keyword) {
        List<Resource> resources = resourceRepository.searchByKeyword(keyword);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> searchResourcesByCourse(Long courseId, String keyword) {
        List<Resource> resources = resourceRepository.searchByCourseAndKeyword(courseId, keyword);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getResourcesByFileType(String fileType) {
        List<Resource> resources = resourceRepository.findByFileType(fileType);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getResourcesByMimeType(String mimeType) {
        List<Resource> resources = resourceRepository.findByMimeType(mimeType);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getPublicResources() {
        List<Resource> resources = resourceRepository.findByIsPublicTrue();
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getPrivateResources() {
        List<Resource> resources = resourceRepository.findByIsPublicFalse();
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getLargestResources() {
        List<Resource> resources = resourceRepository.findLargestResources();
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<ResourceDto> getLargestResourcesByCourse(Long courseId) {
        List<Resource> resources = resourceRepository.findLargestResourcesByCourse(courseId);
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Resource Statistics
    public Long getResourceCount() {
        return resourceRepository.count();
    }
    
    public Long getResourceCountByCourse(Long courseId) {
        return resourceRepository.countByCourseId(courseId);
    }
    
    public Long getResourceCountByFileType(String fileType) {
        return resourceRepository.countByFileType(fileType);
    }
    
    // File Upload Helper
    public ResourceDto uploadFile(MultipartFile file, Long courseId, Long moduleId, Long lessonId) {
        log.info("Uploading file: {} for course: {}", file.getOriginalFilename(), courseId);
        
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        // Generate file URL (in production, this would upload to cloud storage)
        String fileUrl = generateFileUrl(file.getOriginalFilename());
        
        ResourceDto request = ResourceDto.builder()
                .name(file.getOriginalFilename())
                .description("Uploaded file: " + file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(getFileExtension(file.getOriginalFilename()))
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .isPublic(false)
                .courseId(courseId)
                .moduleId(moduleId)
                .lessonId(lessonId)
                .build();
        
        return createResource(request);
    }
    
    // Helper Methods
    private String generateDownloadToken() {
        return UUID.randomUUID().toString();
    }
    
    private String generateFileUrl(String filename) {
        // In production, this would upload to cloud storage and return the URL
        return "/uploads/" + UUID.randomUUID().toString() + "/" + filename;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private ResourceDto mapToDto(Resource resource) {
        return ResourceDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .fileUrl(resource.getFileUrl())
                .fileType(resource.getFileType())
                .mimeType(resource.getMimeType())
                .fileSize(resource.getFileSize())
                .isPublic(resource.getIsPublic())
                .downloadToken(resource.getDownloadToken())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .courseId(resource.getCourse() != null ? resource.getCourse().getId() : null)
                .moduleId(resource.getModule() != null ? resource.getModule().getId() : null)
                .lessonId(resource.getLesson() != null ? resource.getLesson().getId() : null)
                .build();
    }
    
    public List<ResourceDto> getAllResources() {
        List<Resource> resources = resourceRepository.findAllByOrderByCreatedAtDesc();
        return resources.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public Page<ResourceDto> getAllResources(Pageable pageable) {
        Page<Resource> resources = resourceRepository.findAll(pageable);
        return resources.map(this::mapToDto);
    }
} 