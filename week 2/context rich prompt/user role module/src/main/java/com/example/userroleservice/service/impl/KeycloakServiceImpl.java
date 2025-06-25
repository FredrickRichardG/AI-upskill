package com.example.userroleservice.service.impl;

import com.example.userroleservice.dto.CreateUserRequest;
import com.example.userroleservice.dto.UserDto;
import com.example.userroleservice.service.KeycloakService;
import com.example.userroleservice.util.CreatedResponseUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public void createRole(String roleName, String description) {
        try {
            RolesResource rolesResource = getRolesResource();
            
            // Check if role already exists
            if (roleExists(roleName)) {
                log.warn("Role {} already exists in Keycloak", roleName);
                return;
            }

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(description);
            role.setComposite(false);
            role.setClientRole(false);

            rolesResource.create(role);
            log.info("Successfully created role {} in Keycloak", roleName);
        } catch (Exception e) {
            log.error("Error creating role in Keycloak", e);
            throw new RuntimeException("Failed to create role in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public void deleteRole(String roleName) {
        try {
            RolesResource rolesResource = getRolesResource();
            RoleResource roleResource = rolesResource.get(roleName);
            roleResource.remove();
            log.info("Successfully deleted role {} from Keycloak", roleName);
        } catch (Exception e) {
            log.error("Error deleting role from Keycloak", e);
            throw new RuntimeException("Failed to delete role from Keycloak: " + e.getMessage());
        }
    }

    @Override
    public boolean roleExists(String roleName) {
        try {
            RolesResource rolesResource = getRolesResource();
            return rolesResource.list().stream()
                    .anyMatch(role -> role.getName().equals(roleName));
        } catch (Exception e) {
            log.error("Error checking if role exists in Keycloak", e);
            throw new RuntimeException("Failed to check if role exists in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public void createUser(CreateUserRequest request, Set<String> roleNames) {
        try {
            UsersResource usersResource = getUsersResource();
            
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailVerified(true);

            // Create user
            Response response = usersResource.create(user);
            if (response.getStatus() != 201) {
                log.error("Failed to create user in Keycloak. Status: {}", response.getStatus());
                throw new RuntimeException("Failed to create user in Keycloak");
            }

            // Get created user ID
            String userId = CreatedResponseUtil.getCreatedId(response);
            if (userId == null) {
                throw new RuntimeException("Failed to get created user ID from Keycloak");
            }
            
            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            
            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(credential);

            // Assign roles if any
            if (roleNames != null && !roleNames.isEmpty()) {
                assignRoles(userId, roleNames);
            }

            log.info("Successfully created user in Keycloak with ID: {}", userId);
        } catch (Exception e) {
            log.error("Error creating user in Keycloak", e);
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(String keycloakUserId, UserDto userDto) {
        try {
            UserResource userResource = getUsersResource().get(keycloakUserId);
            UserRepresentation user = userResource.toRepresentation();
            
            user.setEmail(userDto.getEmail());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            
            userResource.update(user);
            log.info("Successfully updated user in Keycloak with ID: {}", keycloakUserId);
        } catch (Exception e) {
            log.error("Error updating user in Keycloak", e);
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(String keycloakUserId) {
        try {
            getUsersResource().delete(keycloakUserId);
            log.info("Successfully deleted user from Keycloak with ID: {}", keycloakUserId);
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak", e);
            throw new RuntimeException("Failed to delete user from Keycloak: " + e.getMessage());
        }
    }

    @Override
    public void assignRoles(String keycloakUserId, Set<String> roleNames) {
        try {
            UserResource userResource = getUsersResource().get(keycloakUserId);
            RealmResource realmResource = keycloak.realm(realm);
            
            List<RoleRepresentation> roles = roleNames.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .collect(Collectors.toList());
            
            userResource.roles().realmLevel().add(roles);
            log.info("Successfully assigned roles {} to user {}", roleNames, keycloakUserId);
        } catch (Exception e) {
            log.error("Error assigning roles in Keycloak", e);
            throw new RuntimeException("Failed to assign roles in Keycloak: " + e.getMessage());
        }
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }
} 