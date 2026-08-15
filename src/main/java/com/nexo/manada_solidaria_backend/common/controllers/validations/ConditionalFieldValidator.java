package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.DirectFieldAccessor;

import java.util.Arrays;

public class ConditionalFieldValidator implements ConstraintValidator<ConditionalField, Object> {

    private String[] fields;
    private String dependsOn;
    private String expectedValue;
    private ConditionalField.Rule rule;

    @Override
    public void initialize(ConditionalField annotation) {
        this.fields = annotation.fields().length > 0
                ? annotation.fields()
                : new String[]{annotation.field()};
        this.dependsOn = annotation.dependsOn();
        this.expectedValue = annotation.expectedValue();
        this.rule = annotation.rule();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        DirectFieldAccessor accessor = new DirectFieldAccessor(value);
        boolean matches = expectedValue.equals(String.valueOf(accessor.getPropertyValue(dependsOn)));
        boolean allPresent = Arrays.stream(fields).allMatch(field -> accessor.getPropertyValue(field) != null);
        boolean anyPresent = Arrays.stream(fields).anyMatch(field -> accessor.getPropertyValue(field) != null);

        boolean valid = switch (rule) {
            case REQUIRED -> !matches || allPresent;
            case ONLY_ALLOWED -> matches || !anyPresent;
            case REQUIRED_AND_ONLY_ALLOWED -> (matches && allPresent) || (!matches && !anyPresent);
            case NOT_ALLOWED -> !matches || !anyPresent;
        };
        if (valid) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fields[0])
                .addConstraintViolation();
        return false;
    }
}