package com.lms.apigateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/fallback/user-service")
    public ResponseEntity<String> userServiceFallback() {
        return ResponseEntity.status(503).body("User Service is currently unavailable. Please try again later.");
    }
} 