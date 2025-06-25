package com.example.userroleservice.mapper;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    User toEntity(CreateUserRequest request);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    void updateEntity(UserDto dto, @MappingTarget User user);
} 