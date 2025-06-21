package com.school.attendance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "class_rooms")
public class ClassRoom extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String grade;
    
    @Column(nullable = false)
    private String section;
    
    @Column(nullable = false)
    private int academicYear;
    
    @Column(nullable = false)
    private boolean active = true;
} 