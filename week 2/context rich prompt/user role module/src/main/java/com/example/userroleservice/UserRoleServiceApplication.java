package com.example.userroleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
    info = @Info(
        title = "User Role Service API",
        version = "1.0",
        description = "REST API for User and Role Management"
    )
)
public class UserRoleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserRoleServiceApplication.class, args);
    }
} 