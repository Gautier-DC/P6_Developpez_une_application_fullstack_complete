package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login request
 * Used for POST /auth/login endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @Schema(description = "User's email or username", example = "user@example.com")
    @NotBlank(message = "Email or username is required")
    private String emailOrUsername;

    @Schema(description = "User's password", example = "strongPassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
