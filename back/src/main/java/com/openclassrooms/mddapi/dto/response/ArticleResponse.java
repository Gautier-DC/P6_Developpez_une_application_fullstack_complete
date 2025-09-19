package com.openclassrooms.mddapi.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private ThemeResponse theme;
    private int commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}