package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.CommentService;
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
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Comments", description = "Comment management APIs")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthenticationHelperService authenticationHelperService;

    @PostMapping
    @Operation(summary = "Create a new comment", description = "Create a new comment on an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request,
                                                       Authentication authentication) {
        User user = authenticationHelperService.getCurrentUser(authentication);
        log.info("Creating comment on article ID: {} by user: {}", request.getArticleId(), user.getUsername());

        CommentResponse response = commentService.createComment(request, user);
        log.info("Comment created successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Get comments by article", description = "Retrieve all comments for a specific article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CommentResponse>> getCommentsByArticle(@PathVariable Long articleId) {
        log.info("Fetching comments for article ID: {}", articleId);

        List<CommentResponse> comments = commentService.getCommentsByArticle(articleId);
        log.info("Retrieved {} comments for article ID: {}", comments.size(), articleId);

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID", description = "Retrieve a specific comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment retrieved successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
        log.info("Fetching comment with ID: {}", id);

        CommentResponse comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/my-comments")
    @Operation(summary = "Get current user's comments", description = "Retrieve comments created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CommentResponse>> getMyComments(Authentication authentication) {
        User user = authenticationHelperService.getCurrentUser(authentication);
        log.info("Fetching comments for user: {}", user.getUsername());

        List<CommentResponse> comments = commentService.getCommentsByUser(user.getUsername());
        log.info("Retrieved {} comments for user: {}", comments.size(), user.getUsername());

        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete an existing comment (only by the author)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this comment"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        User user = authenticationHelperService.getCurrentUser(authentication);
        log.info("Deleting comment with ID: {} by user: {}", id, user.getUsername());

        commentService.deleteComment(id, user);
        log.info("Comment deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}