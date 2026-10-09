package com.nexo.manada_solidaria_backend.auth.validations.validators;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.validations.annotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, CreateUserRequest> {

    @Override
    public boolean isValid(CreateUserRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (Objects.equals(value.getPassword(), value.getRepeatedPassword())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("repeatedPassword")
                .addConstraintViolation();

        return false;
    }
}
