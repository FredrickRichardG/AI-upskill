package com.piggymetrics.user.service;

import com.piggymetrics.user.domain.User;
import reactor.core.publisher.Mono;
import java.util.Set;

public interface UserService {
    Mono<User> findById(Long id);
    Mono<User> createUser(User user);
    Mono<User> updateUserRoles(Long id, Set<Long> roleIds);
} 