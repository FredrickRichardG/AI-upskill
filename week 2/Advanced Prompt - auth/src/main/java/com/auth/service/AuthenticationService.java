package com.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Service class handling user authentication operations including email validation,
 * password hashing, and JWT token generation.
 * 
 * @author Your Name
 * @version 1.0
 */
@Slf4j
public class AuthenticationService {
    
    // Email regex pattern following RFC 5322 standards
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );
    
    // Password regex pattern requiring at least 8 characters, one uppercase, one lowercase, 
    // one number and one special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    // JWT token expiration time (24 hours)
    private static final long TOKEN_EXPIRATION_HOURS = 24;
    
    // Secure key for JWT signing
    private final SecretKey jwtSecretKey;
    
    /**
     * Constructor initializing the JWT secret key.
     */
    public AuthenticationService() {
        this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
    
    /**
     * Authenticates a user with email and password, returning a JWT token if successful.
     * 
     * @param email The user's email address
     * @param password The user's password
     * @return CompletableFuture<String> containing the JWT token if authentication is successful
     * @throws AuthenticationException if the credentials are invalid or if validation fails
     */
    public CompletableFuture<String> authenticate(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate input format
                validateCredentials(email, password);
                
                // In a real application, you would fetch the user from a database here
                // For this example, we'll simulate user lookup and password verification
                if (!verifyCredentials(email, password)) {
                    throw new AuthenticationException("Invalid credentials");
                }
                
                // Generate and return JWT token
                return generateJwtToken(email);
                
            } catch (Exception e) {
                log.error("Authentication failed for email: {}", email, e);
                throw new AuthenticationException("Authentication failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Validates the format of email and password using regex patterns.
     * 
     * @param email The email to validate
     * @param password The password to validate
     * @throws AuthenticationException if validation fails
     */
    private void validateCredentials(String email, String password) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new AuthenticationException("Invalid email format");
        }
        
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new AuthenticationException(
                "Password must be at least 8 characters long and contain at least one uppercase letter, " +
                "one lowercase letter, one number and one special character"
            );
        }
    }
    
    /**
     * Verifies user credentials against stored values.
     * In a real application, this would check against a database.
     * 
     * @param email The email to verify
     * @param password The password to verify
     * @return boolean indicating if credentials are valid
     */
    private boolean verifyCredentials(String email, String password) {
        // This is where you would typically fetch the user's hashed password from a database
        // For demonstration, we'll create a dummy hashed password
        String storedHash = BCrypt.hashpw("TestPassword123!", BCrypt.gensalt());
        
        // Verify the provided password against the stored hash
        return BCrypt.checkpw(password, storedHash);
    }
    
    /**
     * Generates a JWT token for the authenticated user.
     * 
     * @param email The email to include in the token
     * @return String containing the JWT token
     */
    private String generateJwtToken(String email) {
        Instant now = Instant.now();
        Instant expiration = now.plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS);
        
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(jwtSecretKey)
            .compact();
    }
}

/**
 * Custom exception class for authentication-related errors.
 */
class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
} 