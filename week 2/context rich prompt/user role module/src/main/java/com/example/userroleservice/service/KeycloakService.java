package com.example.userroleservice.service;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.UserDto;
import java.util.Set;

public interface KeycloakService {
    void createUser(CreateUserRequest request, Set<String> roleNames);
    void updateUser(String keycloakUserId, UserDto userDto);
    void deleteUser(String keycloakUserId);
    void assignRoles(String keycloakUserId, Set<String> roleNames);
    void createRole(String roleName, String description);
    void deleteRole(String roleName);
    boolean roleExists(String roleName);
} 