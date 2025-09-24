package com.openclassrooms.mddapi.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(Long id) {
        super("Comment not found with ID: " + id);
    }
}