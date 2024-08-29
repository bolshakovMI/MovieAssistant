package com.example.movieAssistant.anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImdbIdValidator implements ConstraintValidator<ImdbId, Long> {
    @Override
    public void initialize(ImdbId constraintAnnotation) {
        // Ничего не нужно делать здесь
    }

    @Override
    public boolean isValid(Long imdbId, ConstraintValidatorContext context) {
        if (imdbId == null) {
            return false; // ID не может быть null
        }
        String idString = String.valueOf(imdbId);
        return idString.matches("^[0-9]{7,8}$");
    }
}
