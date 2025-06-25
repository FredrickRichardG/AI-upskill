package com.example.userroleservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;
    
    @NotBlank(message = "Role name is required")
    private String name;
    
    private String description;
} 