package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordConfirmation> {

    @Override
    public boolean isValid(PasswordConfirmation value, ConstraintValidatorContext context) {
        return value == null || Objects.equals(value.getPassword(), value.getRepeatedPassword());
    }
}
