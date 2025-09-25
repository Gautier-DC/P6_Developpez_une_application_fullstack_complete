package com.openclassrooms.mddapi.dto.response;

import com.openclassrooms.mddapi.model.User;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for authentication response
 * Used for login and register endpoints responses
 * Contains JWT token
 */
public class AuthResponse {

    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Type of the token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Username for display", example = "john_doe")
    private String username;

    @Schema(description = "User email", example = "john@example.com")
    private String email;

    @Schema(description = "Token expiration in seconds", example = "86400")
    private Long expiresIn;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String email, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.expiresIn = expiresIn;
    }

    // Static factory methods for different scenarios
    public static AuthResponse fromUser(String token, User user, Long expiresIn) {
        return new AuthResponse(token, user.getUsername(), user.getEmail(), expiresIn);
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
