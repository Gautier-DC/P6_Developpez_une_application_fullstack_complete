package com.openclassrooms.mddapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // Pas d'initialisation nécessaire
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        if (password.length() < 8) {
            return false;
        }

        // Check for at least one digit
        if (!password.matches(".*[0-9].*")) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for at least one special character
        if (!password.matches(".*[@#$%^&+=!?.,:;()\\[\\]{}|\\-_~`].*")) {
            return false;
        }

        return true;
    }
}