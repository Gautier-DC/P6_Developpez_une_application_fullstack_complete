package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.response.ArticleResponse;
import com.openclassrooms.mddapi.dto.response.ThemeResponse;
import com.openclassrooms.mddapi.exception.ArticleNotFoundException;
import com.openclassrooms.mddapi.exception.ThemeNotFoundException;
import com.openclassrooms.mddapi.exception.UnauthorizedOperationException;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Theme;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public ArticleResponse createArticle(CreateArticleRequest request, User author) {
        log.info("Creating new article with title: {} by author: {}", request.getTitle(), author.getUsername());

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new ThemeNotFoundException(request.getThemeId()));

        Article article = new Article(request.getTitle(), request.getContent(), author, theme);
        Article savedArticle = articleRepository.save(article);

        log.info("Article created successfully with ID: {}", savedArticle.getId());
        return convertToResponse(savedArticle);
    }

    @Override
    public List<ArticleResponse> getAllArticles() {
        log.info("Fetching all articles");
        return articleRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleResponse getArticleById(Long id) {
        log.info("Fetching article with ID: {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        return convertToResponse(article);
    }

    @Override
    public List<ArticleResponse> getArticlesByAuthor(User author) {
        log.info("Fetching articles by author: {}", author.getUsername());
        return articleRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleResponse> getArticlesByTheme(Long themeId) {
        log.info("Fetching articles by theme ID: {}", themeId);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeNotFoundException(themeId));

        return articleRepository.findByThemeOrderByCreatedAtDesc(theme)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleResponse> searchArticles(String keyword) {
        log.info("Searching articles with keyword: {}", keyword);
        return articleRepository.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(keyword)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleResponse updateArticle(Long id, CreateArticleRequest request, User author) {
        log.info("Updating article with ID: {} by author: {}", id, author.getUsername());

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        if (!article.getAuthor().getId().equals(author.getId())) {
            throw new UnauthorizedOperationException("User not authorized to update this article");
        }

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new ThemeNotFoundException(request.getThemeId()));

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setTheme(theme);

        Article updatedArticle = articleRepository.save(article);
        log.info("Article updated successfully with ID: {}", updatedArticle.getId());
        return convertToResponse(updatedArticle);
    }

    @Override
    public void deleteArticle(Long id, User author) {
        log.info("Deleting article with ID: {} by author: {}", id, author.getUsername());

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        if (!article.getAuthor().getId().equals(author.getId())) {
            throw new UnauthorizedOperationException("User not authorized to delete this article");
        }

        articleRepository.delete(article);
        log.info("Article deleted successfully with ID: {}", id);
    }

    private ArticleResponse convertToResponse(Article article) {
        ThemeResponse themeResponse = new ThemeResponse(
                article.getTheme().getId(),
                article.getTheme().getName(),
                article.getTheme().getDescription(),
                article.getTheme().getCreatedAt(),
                article.getTheme().getUpdatedAt()
        );

        int commentsCount = (int) commentRepository.countByArticle(article);

        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getAuthor().getUsername(),
                themeResponse,
                commentsCount,
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}