package com.example.userroleservice.service.impl;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.entity.User;
import com.example.userroleservice.entity.Role;
import com.example.userroleservice.mapper.RoleMapper;
import com.example.userroleservice.mapper.UserMapper;
import com.example.userroleservice.repository.UserRepository;
import com.example.userroleservice.repository.RoleRepository;
import com.example.userroleservice.service.UserService;
import com.example.userroleservice.service.KeycloakService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;

    @CachePut(value="USER_CACHE",key = "#result.id")
    @Override
    public UserDto createUser(CreateUserRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        try {
            // Get role names from role IDs
            Set<String> roleNames = null;
            if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
                roleNames = roleRepository.findByIdIn(request.getRoleIds())
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());
            }

            // Create user in Keycloak first
            keycloakService.createUser(request, roleNames);

            // Then create user in local database
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
                Set<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
                user.setRoles(roles);
            }

            User savedUser = userRepository.save(user);
            log.info("Successfully created user with ID: {}", savedUser.getId());
            return userMapper.toDto(savedUser);
        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    @CachePut(value="ROLE_CACHE",key = "#result.id")
    public RoleDto createRole(RoleDto request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role already exists");
        }

        try {
            // Create role in Keycloak first
            keycloakService.createRole(request.getName(), request.getDescription());

            // Then create role in local database
            Role role = roleMapper.toEntity(request);
            Role savedRole = roleRepository.save(role);
            log.info("Successfully created role with ID: {}", savedRole.getId());
            return roleMapper.toDto(savedRole);
        } catch (Exception e) {
            log.error("Error creating role", e);
            throw new RuntimeException("Failed to create role: " + e.getMessage());
        }
    }

    @Cacheable(value="USER_CACHE",key = "#id")
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value="USER_CACHE",key = "#id")
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            // Update user in Keycloak
            if (user.getKeycloakUserId() != null) {
                keycloakService.updateUser(user.getKeycloakUserId(), userDto);
            }

            userMapper.updateEntity(userDto, user);
            User updatedUser = userRepository.save(user);
            log.info("Successfully updated user with ID: {}", id);
            return userMapper.toDto(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user", e);
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    @CacheEvict(value="USER_CACHE",key = "#id")
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            // Delete user from Keycloak
            if (user.getKeycloakUserId() != null) {
                keycloakService.deleteUser(user.getKeycloakUserId());
            }

            userRepository.deleteById(id);
            log.info("Successfully deleted user with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting user", e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    @Override
    public UserDto assignRoles(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<Role> roles = roleRepository.findByIdIn(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new EntityNotFoundException("One or more roles not found");
        }

        try {
            // Assign roles in Keycloak
            if (user.getKeycloakUserId() != null) {
                Set<String> roleNames = roles.stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());
                keycloakService.assignRoles(user.getKeycloakUserId(), roleNames);
            }

            user.setRoles(roles);
            User updatedUser = userRepository.save(user);
            log.info("Successfully assigned roles to user with ID: {}", userId);
            return userMapper.toDto(updatedUser);
        } catch (Exception e) {
            log.error("Error assigning roles to user", e);
            throw new RuntimeException("Failed to assign roles: " + e.getMessage());
        }
    }
} 