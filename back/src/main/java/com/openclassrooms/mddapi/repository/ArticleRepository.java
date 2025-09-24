package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Theme;
import com.openclassrooms.mddapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByAuthor(User author);

    List<Article> findByTheme(Theme theme);

    List<Article> findByThemeOrderByCreatedAtDesc(Theme theme);

    List<Article> findByAuthorOrderByCreatedAtDesc(User author);

    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    List<Article> findAllOrderByCreatedAtDesc();

    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.content LIKE %:keyword% ORDER BY a.createdAt DESC")
    List<Article> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(@Param("keyword") String keyword);
}