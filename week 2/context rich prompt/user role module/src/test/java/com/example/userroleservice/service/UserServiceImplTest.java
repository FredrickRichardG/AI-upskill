package com.example.userroleservice.service;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.entity.Role;
import com.example.userroleservice.entity.User;
import com.example.userroleservice.mapper.RoleMapper;
import com.example.userroleservice.mapper.UserMapper;
import com.example.userroleservice.repository.RoleRepository;
import com.example.userroleservice.repository.UserRepository;
import com.example.userroleservice.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private CreateUserRequest createUserRequest;
    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setActive(true);

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPassword("password");
        createUserRequest.setFirstName("Test");
        createUserRequest.setLastName("User");

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setDescription("Regular user role");

        roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setName("ROLE_USER");
        roleDto.setDescription("Regular user role");
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.createUser(createUserRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(createUserRequest.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.getUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void assignRoles_Success() {
        // Arrange
        Set<Long> roleIds = Set.of(1L);
        Set<Role> roles = Set.of(role);
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findByIdIn(anySet())).thenReturn(roles);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.assignRoles(1L, roleIds);

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void createRole_Success() {
        // Arrange
        when(roleRepository.existsByName(anyString())).thenReturn(false);
        when(roleMapper.toEntity(any(RoleDto.class))).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(any(Role.class))).thenReturn(roleDto);

        // Act
        RoleDto result = userService.createRole(roleDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(roleDto.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_DuplicateName() {
        // Arrange
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createRole(roleDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role already exists");
    }
} 