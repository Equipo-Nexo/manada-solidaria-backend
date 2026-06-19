package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConditionalField.List.class)
@Constraint(validatedBy = ConditionalFieldValidator.class)
public @interface ConditionalField {
    String field();

    String dependsOn();

    String expectedValue();

    Rule rule() default Rule.REQUIRED;

    String message() default "El campo {field} no cumple la condición según {dependsOn}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum Rule {
        REQUIRED,
        ONLY_ALLOWED
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ConditionalField[] value();
    }
}
