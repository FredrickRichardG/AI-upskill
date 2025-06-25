package com.example.userroleservice.service;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.entity.Role;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto createUser(CreateUserRequest request);
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto assignRoles(Long userId, Set<Long> roleIds);
    RoleDto createRole(RoleDto request);
} 