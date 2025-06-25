package com.example.userroleservice.controller;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.RoleDto;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.entity.Role;
import com.example.userroleservice.entity.User;
import com.example.userroleservice.repository.RoleRepository;
import com.example.userroleservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create a test role
        userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setDescription("Regular user role");
        userRole = roleRepository.save(userRole);

        // Create a test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setActive(true);
        testUser.setRoles(Set.of(userRole));
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_Success() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("User");
        request.setRoleIds(Set.of(userRole.getId()));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createRole_Success() throws Exception {
        RoleDto roleDto = new RoleDto();
        roleDto.setName("ROLE_MANAGER");
        roleDto.setDescription("Manager role");

        mockMvc.perform(post("/api/users/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ROLE_MANAGER"))
                .andExpect(jsonPath("$.description").value("Manager role"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.active").value(testUser.isActive()))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0].name").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].username").value(testUser.getUsername()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void assignRoles_Success() throws Exception {
        // Create a new role
        Role managerRole = new Role();
        managerRole.setName("ROLE_MANAGER");
        managerRole.setDescription("Manager role");
        managerRole = roleRepository.save(managerRole);

        Set<Long> roleIds = Set.of(userRole.getId(), managerRole.getId());

        mockMvc.perform(put("/api/users/{id}/roles", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("ROLE_USER", "ROLE_MANAGER")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNotFound());
    }
} 