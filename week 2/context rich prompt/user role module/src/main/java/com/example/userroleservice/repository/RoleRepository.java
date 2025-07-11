package com.example.userroleservice.repository;

import com.example.userroleservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Set<Role> findByIdIn(Set<Long> ids);
    boolean existsByName(String name);
}