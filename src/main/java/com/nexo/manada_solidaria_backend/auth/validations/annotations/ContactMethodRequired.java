package com.nexo.manada_solidaria_backend.auth.validations.annotations;

import com.nexo.manada_solidaria_backend.auth.validations.validators.ContactRequiredValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContactRequiredValidator.class)
public @interface ContactMethodRequired {
    String message() default "Debe ingresar un método de contacto, email o número de teléfono";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
