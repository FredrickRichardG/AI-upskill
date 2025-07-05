package com.lms.courseservice.service;

import com.lms.courseservice.dto.InstructorDto;
import com.lms.courseservice.model.Instructor;
import com.lms.courseservice.repository.InstructorRepository;
import com.lms.courseservice.repository.CourseAssignmentRepository;
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
public class InstructorService {
    
    private final InstructorRepository instructorRepository;
    private final CourseAssignmentRepository assignmentRepository;
    
    // Instructor CRUD Operations
    public InstructorDto createInstructor(InstructorDto request) {
        log.info("Creating new instructor: {} {}", request.getFirstName(), request.getLastName());
        
        // Check if email already exists
        if (instructorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Instructor with this email already exists");
        }
        
        Instructor instructor = Instructor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .expertise(request.getExpertise())
                .qualification(request.getQualification())
                .yearsOfExperience(request.getYearsOfExperience())
                .linkedinUrl(request.getLinkedinUrl())
                .twitterUrl(request.getTwitterUrl())
                .websiteUrl(request.getWebsiteUrl())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Instructor savedInstructor = instructorRepository.save(instructor);
        return mapToDto(savedInstructor);
    }
    
    public InstructorDto updateInstructor(Long instructorId, InstructorDto request) {
        log.info("Updating instructor: {}", instructorId);
        
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        
        instructor.setFirstName(request.getFirstName());
        instructor.setLastName(request.getLastName());
        instructor.setEmail(request.getEmail());
        instructor.setBio(request.getBio());
        instructor.setProfileImageUrl(request.getProfileImageUrl());
        instructor.setExpertise(request.getExpertise());
        instructor.setQualification(request.getQualification());
        instructor.setYearsOfExperience(request.getYearsOfExperience());
        instructor.setLinkedinUrl(request.getLinkedinUrl());
        instructor.setTwitterUrl(request.getTwitterUrl());
        instructor.setWebsiteUrl(request.getWebsiteUrl());
        instructor.setIsActive(request.getIsActive());
        instructor.setUpdatedAt(LocalDateTime.now());
        
        Instructor savedInstructor = instructorRepository.save(instructor);
        return mapToDto(savedInstructor);
    }
    
    public InstructorDto getInstructorById(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return mapToDto(instructor);
    }
    
    public InstructorDto getInstructorByEmail(String email) {
        Instructor instructor = instructorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return mapToDto(instructor);
    }
    
    public List<InstructorDto> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAllOrderByName();
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public Page<InstructorDto> getAllInstructors(Pageable pageable) {
        Page<Instructor> instructors = instructorRepository.findAll(pageable);
        return instructors.map(this::mapToDto);
    }
    
    public void deleteInstructor(Long instructorId) {
        log.info("Deleting instructor: {}", instructorId);
        
        // Check if instructor has course assignments
        long assignmentCount = assignmentRepository.countByInstructorId(instructorId);
        if (assignmentCount > 0) {
            throw new RuntimeException("Cannot delete instructor with existing course assignments");
        }
        
        instructorRepository.deleteById(instructorId);
    }
    
    // Instructor Search
    public List<InstructorDto> searchInstructors(String keyword) {
        List<Instructor> instructors = instructorRepository.searchByKeyword(keyword);
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Instructor Statistics
    public List<InstructorDto> getInstructorsWithCourseCount() {
        List<Object[]> results = instructorRepository.findInstructorsWithCourseCount();
        return results.stream()
                .map(result -> {
                    Instructor instructor = (Instructor) result[0];
                    Long courseCount = (Long) result[1];
                    InstructorDto dto = mapToDto(instructor);
                    dto.setCourseCount(courseCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public List<InstructorDto> getTopInstructors() {
        List<Instructor> instructors = instructorRepository.findTopInstructors();
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<InstructorDto> getInstructorsWithPublishedCourses() {
        List<Instructor> instructors = instructorRepository.findInstructorsWithPublishedCourses();
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<InstructorDto> getActiveInstructors() {
        List<Instructor> instructors = instructorRepository.findByIsActiveTrue();
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<InstructorDto> getInstructorsByExpertise(String expertise) {
        List<Instructor> instructors = instructorRepository.findByExpertiseContainingIgnoreCase(expertise);
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<InstructorDto> getInstructorsByExperienceRange(Integer minExperience, Integer maxExperience) {
        List<Instructor> instructors = instructorRepository.findByYearsOfExperienceBetween(minExperience, maxExperience);
        return instructors.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Instructor Statistics
    public Long getInstructorCount() {
        return instructorRepository.count();
    }
    
    public Long getActiveInstructorCount() {
        return instructorRepository.countByIsActiveTrue();
    }
    
    // Helper Methods
    private InstructorDto mapToDto(Instructor instructor) {
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
                .courseCount(assignmentRepository.countByInstructorId(instructor.getId()))
                .publishedCourseCount(assignmentRepository.countByInstructorIdAndRole(instructor.getId(), 
                        com.lms.courseservice.model.AssignmentRole.INSTRUCTOR))
                .build();
    }
} 