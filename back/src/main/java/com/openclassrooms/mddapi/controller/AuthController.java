package com.openclassrooms.mddapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.dto.response.UserResponse;
import com.openclassrooms.mddapi.service.AuthService;
import com.openclassrooms.mddapi.service.AuthenticationHelperService;

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
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationHelperService authenticationHelperService;

    public AuthController(AuthService authService, AuthenticationHelperService authenticationHelperService) {
        this.authService = authService;
        this.authenticationHelperService = authenticationHelperService;
    }

    /**
     * Register a new user
     * 
     * @param registerRequest User registration data
     * @param bindingResult   Validation result
     * @return AuthResponse with JWT token and user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account and return JWT token for immediate login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for email: {}", registerRequest.getEmail());
        
        AuthResponse response = authService.register(registerRequest);
        log.info("User registered successfully: {}", registerRequest.getEmail());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest  User login credentials
     * @param bindingResult Validation result
     * @return AuthResponse with JWT token and user info
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user credentials and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        
        AuthResponse response = authService.login(loginRequest);
        log.info("User logged in successfully: {}", loginRequest.getEmail());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user profile information
     * 
     * @param authentication Spring Security authentication object
     * @return UserResponse with current user details
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieve profile information for the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authenticationHelperService.getCurrentUserEmail(authentication);
        log.debug("Fetching profile for user: {}", email);

        UserResponse userResponse = authService.getCurrentUser(email);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Update current user profile information
     * 
     * @param updateRequest Profile update data (username, email, password)
     * @param authentication Spring Security authentication object
     * @return Updated UserResponse
     */
    @PutMapping("/update-profile")
    @Operation(summary = "Update user profile", description = "Update profile information for the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or username/email already exists"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest updateRequest,
                                                     Authentication authentication) {
        try {
            String email = authenticationHelperService.getCurrentUserEmail(authentication);
            log.info("Profile update request for user: {}", email);
            log.debug("Request data - username: {}, email: {}, password provided: {}", 
                     updateRequest.getUsername(), 
                     updateRequest.getEmail(), 
                     updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty());
            
            UserResponse updatedUser = authService.updateProfile(email, updateRequest);
            log.info("Profile updated successfully for user: {}", email);
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (Exception e) {
            log.error("Error during profile update: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Logout user by invalidating JWT token
     * 
     * @param request HTTP request to extract Authorization header
     * @return Success message
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate JWT token by adding it to blacklist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid authorization header"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> logout(HttpServletRequest request) {
        log.info("Logout request received");

        authService.logout(request);

        return ResponseEntity.ok("Logout successful");
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

}