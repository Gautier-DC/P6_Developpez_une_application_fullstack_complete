package com.openclassrooms.mddapi.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private String username;
    private Long articleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}