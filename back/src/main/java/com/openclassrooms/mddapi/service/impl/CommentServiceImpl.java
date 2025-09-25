package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.exception.ArticleNotFoundException;
import com.openclassrooms.mddapi.exception.CommentNotFoundException;
import com.openclassrooms.mddapi.exception.UnauthorizedOperationException;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public CommentResponse createComment(CreateCommentRequest request, User user) {
        log.info("Creating new comment on article ID: {} by user: {}", request.getArticleId(), user.getUsername());

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new ArticleNotFoundException(request.getArticleId()));

        Comment comment = new Comment(request.getContent(), user, article);
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment created successfully with ID: {}", savedComment.getId());
        return convertToResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getCommentsByArticle(Long articleId) {
        log.info("Fetching comments for article ID: {}", articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        return commentRepository.findByArticleOrderByCreatedAtAsc(article)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(Long id) {
        log.info("Fetching comment with ID: {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        return convertToResponse(comment);
    }

    @Override
    public void deleteComment(Long id, User user) {
        log.info("Deleting comment with ID: {} by user: {}", id, user.getUsername());

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedOperationException("User not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully with ID: {}", id);
    }

    @Override
    public List<CommentResponse> getCommentsByUser(String username) {
        log.info("Fetching comments by user: {}", username);
        return commentRepository.findByAuthor_Username(username)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getUsername(),
                comment.getArticle().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}