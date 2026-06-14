package com.nexo.manada_solidaria_backend.auth.validations.validators;


import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.validations.annotations.ContactMethodRequired;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContactRequiredValidator implements ConstraintValidator<ContactMethodRequired, CreateUserRequest> {
    @Override
    public boolean isValid(CreateUserRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.getEmail() != null || value.getPhoneNumber() != null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("contactMethod")
                .addConstraintViolation();

        return false;
    }
}
