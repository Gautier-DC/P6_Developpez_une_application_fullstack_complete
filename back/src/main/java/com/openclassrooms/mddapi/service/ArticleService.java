package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleResponse;
import com.openclassrooms.mddapi.model.User;

import java.util.List;

public interface ArticleService {

    ArticleResponse createArticle(CreateArticleRequest request, User author);

    List<ArticleResponse> getAllArticles();

    ArticleResponse getArticleById(Long id);

    List<ArticleResponse> getArticlesByAuthor(User author);

    List<ArticleResponse> getArticlesByTheme(Long themeId);

    List<ArticleResponse> searchArticles(String keyword);

    ArticleResponse updateArticle(Long id, CreateArticleRequest request, User author);

    void deleteArticle(Long id, User author);
}