package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {

    String startField();

    String endField();

    boolean allowNullStart() default true;

    String message() default "La fecha de fin debe ser posterior a la fecha de inicio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}