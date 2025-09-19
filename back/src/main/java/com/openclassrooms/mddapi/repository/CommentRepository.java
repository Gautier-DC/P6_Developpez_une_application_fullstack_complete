package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticle(Article article);

    List<Comment> findByArticleOrderByCreatedAtAsc(Article article);

    List<Comment> findByUsername(String username);

    @Query("SELECT c FROM Comment c WHERE c.article = :article ORDER BY c.createdAt ASC")
    List<Comment> findByArticleOrderByCreatedAt(Article article);

    long countByArticle(Article article);
}