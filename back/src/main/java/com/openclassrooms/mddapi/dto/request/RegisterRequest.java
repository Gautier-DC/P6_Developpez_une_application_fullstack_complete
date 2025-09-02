package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 
 * DTO for user registration request
 * Used for POST /auth/register endpoint
 */
public class RegisterRequest {
    
    @Schema(description = "User's email", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "User's username", example = "username123")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "User's password", example = "strongPassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max=100, message = "Password must be between 8 and 100 characters long and must contain number, uppercase letters and special characters")
    private String password;

    public RegisterRequest() {}

    public RegisterRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
