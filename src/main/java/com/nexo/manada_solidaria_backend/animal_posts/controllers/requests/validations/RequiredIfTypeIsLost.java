package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredIfTypeIsLostValidator.class)
public @interface RequiredIfTypeIsLost {
    String message() default "Tiene dueño es obligatorio cuando el tipo de publicación es LOST";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
