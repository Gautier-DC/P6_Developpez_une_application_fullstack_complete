package com.openclassrooms.mddapi.exception;

public class ThemeAlreadyExistsException extends RuntimeException {
    public ThemeAlreadyExistsException(String message) {
        super(message);
    }

    public static ThemeAlreadyExistsException withName(String name) {
        return new ThemeAlreadyExistsException("Theme with name '" + name + "' already exists");
    }
}