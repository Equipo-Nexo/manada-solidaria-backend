package com.nexo.manada_solidaria_backend.campaigns.controllers.requests.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CampaignTypeValidationValidator.class)
public @interface CampaignTypeValidation {

    String message() default
            "Los campos enviados no son válidos para el TYPE 'NEWS'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
