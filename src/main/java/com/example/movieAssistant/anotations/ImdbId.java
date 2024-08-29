package com.example.movieAssistant.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImdbIdValidator.class)
public @interface ImdbId {
    String message() default "ID фильма должен быть 7 или 8 значным числом";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
