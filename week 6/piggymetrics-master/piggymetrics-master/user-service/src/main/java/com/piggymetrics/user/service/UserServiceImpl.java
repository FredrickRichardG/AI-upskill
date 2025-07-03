package com.piggymetrics.user.service;

import com.piggymetrics.user.domain.Role;
import com.piggymetrics.user.domain.User;
import com.piggymetrics.user.repository.RoleRepository;
import com.piggymetrics.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Mono<User> findById(Long id) {
        return Mono.fromCallable(() -> userRepository.findById(id).orElse(null));
    }

    @Override
    public Mono<User> createUser(User user) {
        // In a real app, you'd encode the password here
        return Mono.fromCallable(() -> userRepository.save(user));
    }

    @Override
    public Mono<User> updateUserRoles(Long id, Set<Long> roleIds) {
        return Mono.fromCallable(() -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
            return userRepository.save(user);
        });
    }
} 