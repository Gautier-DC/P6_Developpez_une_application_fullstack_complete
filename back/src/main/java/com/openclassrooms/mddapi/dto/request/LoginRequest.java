package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * DTO for user login request
 * Used for POST /auth/login endpoint
 */
public class LoginRequest {
    
    @Schema(description = "User's email or username", example = "user@example.com")
    @NotBlank(message = "Email or username is required")
    private String emailOrUsername;

    @Schema(description = "User's password", example = "strongPassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String emailOrUsername, String password) {
        this.emailOrUsername = emailOrUsername;
        this.password = password;
    }

    // Getters and Setters

    public String getEmailOrUsername() {
        return emailOrUsername;
    }

    public void setEmailOrUsername(String emailOrUsername) {
        this.emailOrUsername = emailOrUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "emailOrUsername='" + emailOrUsername + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }

}
