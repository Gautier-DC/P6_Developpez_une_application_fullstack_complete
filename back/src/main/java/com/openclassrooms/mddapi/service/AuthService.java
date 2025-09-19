package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.dto.response.UserResponse;

public interface AuthService {
    
    /**
     * Register a new user in the system
     * Validates email uniqueness and creates user with encoded password
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Authenticate user and generate JWT token
     * 
     * @param request Login request containing email and password
     * @return AuthResponse with JWT token and user info
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Get current user information by email
     * Used for protected endpoints to return user profile
     */
    UserResponse getCurrentUser(String email);
    
    /**
     * Get user information by ID
     */
    UserResponse getUserById(Long id);
    
    /**
     * Update user profile information
     * 
     * @param email Current user email
     * @param request Update profile request with optional fields
     * @return Updated user information
     */
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    
    /**
     * Logout user by blacklisting their JWT token
     *
     * @param token JWT token to invalidate
     */
    void logout(String token);

    /**
     * Logout user by extracting and blacklisting JWT token from request
     *
     * @param request HTTP request containing Authorization header
     */
    void logout(jakarta.servlet.http.HttpServletRequest request);
}