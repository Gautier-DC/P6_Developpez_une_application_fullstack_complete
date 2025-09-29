package com.openclassrooms.mddapi.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.mddapi.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
