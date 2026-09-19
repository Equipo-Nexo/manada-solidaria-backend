package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "Debe ingresar una contraseña")
@Size(min = 8, message = "Mínimo 8 caracteres")
@Pattern(regexp = ".*[A-Z].*", message = "Una letra mayúscula")
@Pattern(regexp = ".*[a-z].*", message = "Una letra minúscula")
@Pattern(regexp = ".*\\d.*", message = "Un número")
@Pattern(regexp = ".*[^A-Za-z0-9].*", message = "Un carácter especial")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SecurePasswordValidator.class)
public @interface SecurePassword {

    String message() default "La contraseña no puede superar los 64 caracteres";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
