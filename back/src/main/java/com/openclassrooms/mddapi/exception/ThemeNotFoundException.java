package com.openclassrooms.mddapi.exception;

public class ThemeNotFoundException extends RuntimeException {
    public ThemeNotFoundException(String message) {
        super(message);
    }

    public ThemeNotFoundException(Long id) {
        super("Theme not found with ID: " + id);
    }
}