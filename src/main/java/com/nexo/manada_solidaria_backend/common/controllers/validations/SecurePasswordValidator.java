package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

public class SecurePasswordValidator implements ConstraintValidator<SecurePassword, String> {

    private static final int MAX_CHARACTERS = 64;
    private static final int BCRYPT_MAX_BYTES = 72;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null
                || (value.length() <= MAX_CHARACTERS
                && value.getBytes(StandardCharsets.UTF_8).length <= BCRYPT_MAX_BYTES);
    }
}
