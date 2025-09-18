package com.openclassrooms.mddapi.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.mddapi.model.User;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserResponse {

    @Schema(description = "User's ID", example = "1")
    private Long id;

    @Schema(description = "User's username", example = "username123")
    private String username;

    @Schema(description = "User's email", example = "user@example.com")
    private String email;

    @JsonProperty("created_at")
    @Schema(description = "Account creation timestamp", example = "2023-10-05T14:48:00Z")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Account update timestamp", example = "2023-10-05T14:48:00Z")
    private LocalDateTime updatedAt;

    public UserResponse() {}

    public UserResponse(Long id, String email, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor from User entity
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    // Static factory method (alternative to constructor)
    public static UserResponse fromUser(User user) {
        return new UserResponse(user);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

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
}
