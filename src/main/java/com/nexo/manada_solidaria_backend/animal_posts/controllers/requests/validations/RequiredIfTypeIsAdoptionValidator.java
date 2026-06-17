package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredIfTypeIsAdoptionValidator implements ConstraintValidator<RequiredIfTypeIsAdoption, CreateAnimalPostRequest> {

    @Override
    public boolean isValid(CreateAnimalPostRequest request, ConstraintValidatorContext context) {
        if (request == null || request.type() != AnimalPostType.ADOPTION || request.inTransit() != null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("inTransit")
                .addConstraintViolation();
        return false;
    }
}
