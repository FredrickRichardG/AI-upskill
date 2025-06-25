package com.example.userroleservice.mapper;

import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleDto dto);
    
    RoleDto toDto(Role role);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntity(RoleDto roleDto, @MappingTarget Role role);
} 