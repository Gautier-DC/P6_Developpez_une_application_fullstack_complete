package com.openclassrooms.mddapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.dto.response.UserResponse;
import com.openclassrooms.mddapi.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Authentication Controller
 * Handles user registration, login, and profile management
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user
     * 
     * @param registerRequest User registration data
     * @param bindingResult   Validation result
     * @return AuthResponse with JWT token and user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", 
               description = "Create a new user account and return JWT token for immediate login")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "User successfully registered",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data or email already exists"),
        @ApiResponse(responseCode = "500", 
                    description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
                                    BindingResult bindingResult) {
        
        logger.info("Registration attempt for email: {}", registerRequest.getEmail());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Registration validation failed for email: {}", registerRequest.getEmail());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation failed", getValidationErrors(bindingResult)));
        }

        try {
            AuthResponse response = authService.register(registerRequest);
            logger.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed - {}: {}", e.getMessage(), registerRequest.getEmail());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registration failed", e.getMessage()));
                    
        } catch (Exception e) {
            logger.error("Unexpected error during registration for {}: {}", 
                        registerRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "Registration temporarily unavailable"));
        }
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest User login credentials
     * @param bindingResult Validation result
     * @return AuthResponse with JWT token and user info
     */
    @PostMapping("/login")
    @Operation(summary = "User login", 
               description = "Authenticate user credentials and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Login successful",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data"),
        @ApiResponse(responseCode = "401", 
                    description = "Invalid credentials"),
        @ApiResponse(responseCode = "500", 
                    description = "Internal server error")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                 BindingResult bindingResult) {
        
        logger.info("Login attempt for email: {}", loginRequest.getEmail());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Login validation failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation failed", getValidationErrors(bindingResult)));
        }

        try {
            AuthResponse response = authService.login(loginRequest);
            logger.info("User logged in successfully: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.warn("Login failed for {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", "Invalid email or password"));
                    
        } catch (Exception e) {
            logger.error("Unexpected error during login for {}: {}", 
                        loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "Login temporarily unavailable"));
        }
    }

    /**
     * Get current user profile information
     * 
     * @param authentication Spring Security authentication object
     * @return UserResponse with current user details
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", 
               description = "Retrieve profile information for the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", 
                    description = "Not authenticated"),
        @ApiResponse(responseCode = "404", 
                    description = "User not found")
    })
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated", "Please login to access this resource"));
        }

        try {
            String email = authentication.getName();
            logger.debug("Fetching profile for user: {}", email);
            
            UserResponse userResponse = authService.getCurrentUser(email);
            return ResponseEntity.ok(userResponse);
            
        } catch (Exception e) {
            logger.error("Error fetching user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", "Profile information unavailable"));
        }
    }

    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication service health check")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running");
    }

    /**
     * Extract validation error messages from BindingResult
     */
    private String getValidationErrors(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        bindingResult.getAllErrors().forEach(error -> {
            if (errors.length() > 0) errors.append("; ");
            errors.append(error.getDefaultMessage());
        });
        return errors.toString();
    }

    /**
     * Standard error response structure
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        // Getters and setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}