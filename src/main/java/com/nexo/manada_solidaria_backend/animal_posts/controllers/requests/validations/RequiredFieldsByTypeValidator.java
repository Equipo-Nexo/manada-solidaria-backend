package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredFieldsByTypeValidator implements ConstraintValidator<RequiredFieldsByType, CreateAnimalPostRequest> {

    private static final String HAS_OWNER_REQUIRED =
            "El parámetro hasOwner es obligatorio cuando el tipo de publicación es LOST";
    private static final String IN_TRANSIT_REQUIRED =
            "El parámetro inTransit es obligatorio cuando el tipo de publicación es ADOPTION";

    @Override
    public boolean isValid(CreateAnimalPostRequest request, ConstraintValidatorContext context) {
        if (request == null || request.type() == null) {
            return true;
        }
        return switch (request.type()) {
            case LOST -> requireField(request.hasOwner(), "hasOwner", HAS_OWNER_REQUIRED, context);
            case ADOPTION -> requireField(request.inTransit(), "inTransit", IN_TRANSIT_REQUIRED, context);
        };
    }
    
    private boolean requireField(Object value, String field, String message, ConstraintValidatorContext context) {
        if (value != null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
        return false;
    }
}
