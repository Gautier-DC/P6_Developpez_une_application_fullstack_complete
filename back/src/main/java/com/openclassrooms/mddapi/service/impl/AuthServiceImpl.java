package com.openclassrooms.mddapi.service.impl;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.dto.response.UserResponse;
import com.openclassrooms.mddapi.exception.InvalidAuthorizationHeaderException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.AuthService;
import com.openclassrooms.mddapi.service.JwtService;
import com.openclassrooms.mddapi.service.TokenBlacklistService;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    
    @Value("${jwt.expiration}")
    private Long jwtExpirationInMs;

    public AuthServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * Register a new user in the system
     * Validates email uniqueness and creates user with encoded password
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        //Save user to Database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.fromUser(token, savedUser, jwtExpirationInMs / 1000);
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param request Login request containing email and password
     * @return AuthResponse with JWT token and user info
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user with email: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getEmail()));

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());
        
        log.info("User logged in successfully: {}", request.getEmail());
        return AuthResponse.fromUser(token, user, jwtExpirationInMs / 1000);
    }

    /**
     * Get current user information by email
     * Used for protected endpoints to return user profile
     */
    @Override
    public UserResponse getCurrentUser(String email) {
        log.debug("Fetching current user info for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user info for ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
    
    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", email);
        log.debug("Update request data: username={}, email={}, password provided={}",
                request.getUsername(),
                request.getEmail(),
                request.getPassword() != null && !request.getPassword().isEmpty());
        
        try {
            // Find the user by email
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
            
            log.debug("Found user: id={}, email={}, username={}", user.getId(), user.getEmail(), user.getUsername());
        
        boolean isUpdated = false;
        
        // Update username if provided
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            String newUsername = request.getUsername().trim();
            if (!newUsername.equals(user.getUsername())) {
                // Check if username is already taken
                if (userRepository.findByUsername(newUsername).isPresent()) {
                    throw new UserAlreadyExistsException("Username already exists: " + newUsername);
                }
                user.setUsername(newUsername);
                isUpdated = true;
                log.info("Username updated to: {}", newUsername);
            }
        }
        
        // Update email if provided
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equals(user.getEmail())) {
                // Check if email is already taken
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    throw new UserAlreadyExistsException("Email already exists: " + newEmail);
                }
                user.setEmail(newEmail);
                isUpdated = true;
                log.info("Email updated to: {}", newEmail);
            }
        }
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword().trim());
            user.setPassword(encodedPassword);
            isUpdated = true;
            log.info("Password updated for user: {}", email);
        }
        
        // Save only if there were changes
        if (isUpdated) {
            // Remove manual timestamp setting - let @UpdateTimestamp handle it
            user = userRepository.save(user);
            log.info("Profile updated successfully for user: {}", email);
        } else {
            log.info("No changes detected for user profile: {}", email);
        }
        
            // Return updated user response
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getCreatedAt(),
                    user.getUpdatedAt());
                    
        } catch (Exception e) {
            log.error("Error updating profile for user {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public void logout(String token) {
        log.info("Processing logout request");

        // Extract expiration date from token for cleanup purposes
        Date expiration = jwtService.extractExpiration(token);

        // Add token to blacklist
        tokenBlacklistService.blacklistToken(token, expiration);

        log.info("User logged out successfully. Token blacklisted.");
    }

    @Override
    public void logout(jakarta.servlet.http.HttpServletRequest request) {
        log.info("Processing logout request from HTTP request");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Logout attempt without valid Authorization header");
            throw new InvalidAuthorizationHeaderException("No valid authorization header provided");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        logout(token);

        log.info("User logout successful");
    }
}