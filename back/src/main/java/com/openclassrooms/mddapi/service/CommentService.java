package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.model.User;

import java.util.List;

public interface CommentService {

    CommentResponse createComment(CreateCommentRequest request, User user);

    List<CommentResponse> getCommentsByArticle(Long articleId);

    CommentResponse getCommentById(Long id);

    void deleteComment(Long id, User user);

    List<CommentResponse> getCommentsByUser(String username);
}