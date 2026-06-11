package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredIfTypeIsLostValidator implements ConstraintValidator<RequiredIfTypeIsLost, CreateAnimalPostRequest> {

    @Override
    public boolean isValid(CreateAnimalPostRequest request, ConstraintValidatorContext context) {
        if (request == null || request.type() != AnimalPostType.LOST || request.hasOwner() != null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("hasOwner")
                .addConstraintViolation();
        return false;
    }
}
