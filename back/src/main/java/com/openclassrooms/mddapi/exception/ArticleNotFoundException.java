package com.openclassrooms.mddapi.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(String message) {
        super(message);
    }

    public ArticleNotFoundException(Long id) {
        super("Article not found with ID: " + id);
    }
}