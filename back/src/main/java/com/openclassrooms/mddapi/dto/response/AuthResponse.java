package com.openclassrooms.mddapi.dto.response;

import com.openclassrooms.mddapi.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response
 * Used for login and register endpoints responses
 * Contains JWT token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // Constructor for required fields only (excluding tokenType which has default value)
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
}
