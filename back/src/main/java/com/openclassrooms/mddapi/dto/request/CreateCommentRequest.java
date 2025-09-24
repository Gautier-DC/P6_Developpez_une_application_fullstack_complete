package com.openclassrooms.mddapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Article ID is mandatory")
    private Long articleId;
}