package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleResponse;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.ArticleService;
import com.openclassrooms.mddapi.service.AuthenticationHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Articles", description = "Article management APIs")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AuthenticationHelperService authenticationHelperService;

    @PostMapping
    @Operation(summary = "Create a new article", description = "Create a new article with the authenticated user as author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Article created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Theme not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody CreateArticleRequest request,
                                                       Authentication authentication) {
        User author = authenticationHelperService.getCurrentUser(authentication);
        log.info("Creating article '{}' by user: {}", request.getTitle(), author.getUsername());

        ArticleResponse response = articleService.createArticle(request, author);
        log.info("Article created successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all articles", description = "Retrieve all articles ordered by creation date (newest first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        log.info("Fetching all articles");

        List<ArticleResponse> articles = articleService.getAllArticles();
        log.info("Retrieved {} articles", articles.size());

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get article by ID", description = "Retrieve a specific article by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article retrieved successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        log.info("Fetching article with ID: {}", id);

        ArticleResponse article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/my-articles")
    @Operation(summary = "Get current user's articles", description = "Retrieve articles created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ArticleResponse>> getMyArticles(Authentication authentication) {
        User author = authenticationHelperService.getCurrentUser(authentication);
        log.info("Fetching articles for user: {}", author.getUsername());

        List<ArticleResponse> articles = articleService.getArticlesByAuthor(author);
        log.info("Retrieved {} articles for user: {}", articles.size(), author.getUsername());

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/by-theme/{themeId}")
    @Operation(summary = "Get articles by theme", description = "Retrieve all articles for a specific theme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Theme not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ArticleResponse>> getArticlesByTheme(@PathVariable Long themeId) {
        log.info("Fetching articles for theme ID: {}", themeId);

        List<ArticleResponse> articles = articleService.getArticlesByTheme(themeId);
        log.info("Retrieved {} articles for theme ID: {}", articles.size(), themeId);

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search")
    @Operation(summary = "Search articles", description = "Search articles by keyword in title or content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameter"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ArticleResponse>> searchArticles(@RequestParam String keyword) {
        log.info("Searching articles with keyword: {}", keyword);

        List<ArticleResponse> articles = articleService.searchArticles(keyword);
        log.info("Found {} articles matching keyword: {}", articles.size(), keyword);

        return ResponseEntity.ok(articles);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update article", description = "Update an existing article (only by the author)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article updated successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this article"),
            @ApiResponse(responseCode = "404", description = "Article or theme not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id,
                                                       @Valid @RequestBody CreateArticleRequest request,
                                                       Authentication authentication) {
        User author = authenticationHelperService.getCurrentUser(authentication);
        log.info("Updating article with ID: {} by user: {}", id, author.getUsername());

        ArticleResponse response = articleService.updateArticle(id, request, author);
        log.info("Article updated successfully with ID: {}", response.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete article", description = "Delete an existing article (only by the author)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this article"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, Authentication authentication) {
        User author = authenticationHelperService.getCurrentUser(authentication);
        log.info("Deleting article with ID: {} by user: {}", id, author.getUsername());

        articleService.deleteArticle(id, author);
        log.info("Article deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}