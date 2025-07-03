package com.piggymetrics.user.controller;

import com.piggymetrics.user.domain.User;
import com.piggymetrics.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("#oauth2.hasScope('server') or #id == authentication.principal")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("#oauth2.hasScope('server')")
    public Mono<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{id}/roles")
    @PreAuthorize("#oauth2.hasScope('server')")
    public Mono<ResponseEntity<User>> updateUserRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        return userService.updateUserRoles(id, roleIds)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 