package com.openclassrooms.mddapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateArticleRequest {

    @NotBlank(message = "Title is mandatory")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Theme ID is mandatory")
    private Long themeId;
}