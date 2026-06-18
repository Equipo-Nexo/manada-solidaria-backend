package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredFieldsByTypeValidator.class)
public @interface RequiredFieldsByType {
    // message/groups/payload son obligatorios por la spec de Bean Validation, el mensaje real lo arma el validator por cada campo
    String message() default "Faltan campos obligatorios según el tipo de publicación";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
