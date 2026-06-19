package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Repeatable(RequiredField.List.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredFieldValidator.class)
public @interface RequiredField {
    String message() default "El campo {field} es obligatorio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String dependsOn();

    String expectedValue();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        RequiredField[] value();
    }
}
