package com.openclassrooms.mddapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.,:;()\\[\\]{}|\\-_~`])(?=\\S+$).{8,}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // Pas d'initialisation nécessaire
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Si le password est null ou vide, on accepte (champ optionnel pour l'update)
        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        // Vérifier la longueur minimum
        if (password.length() < 8) {
            return false;
        }

        // Vérifier la présence d'au moins un chiffre
        if (!password.matches(".*[0-9].*")) {
            return false;
        }

        // Vérifier la présence d'au moins une lettre minuscule
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Vérifier la présence d'au moins une lettre majuscule
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Vérifier la présence d'au moins un caractère spécial
        if (!password.matches(".*[@#$%^&+=!?.,:;()\\[\\]{}|\\-_~`].*")) {
            return false;
        }

        return true;
    }
}