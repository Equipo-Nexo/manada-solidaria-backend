package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.DirectFieldAccessor;

public class ConditionalFieldValidator implements ConstraintValidator<ConditionalField, Object> {

    private String field;
    private String dependsOn;
    private String expectedValue;
    private ConditionalField.Rule rule;

    @Override
    public void initialize(ConditionalField annotation) {
        this.field = annotation.field();
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
        boolean present = accessor.getPropertyValue(field) != null;

        boolean valid = switch (rule) {
            case REQUIRED -> !matches || present;
            case ONLY_ALLOWED -> matches || !present;
            case REQUIRED_AND_ONLY_ALLOWED -> (matches && present) || (!matches && !present);
            case NOT_ALLOWED -> !matches || !present;
        };
        if (valid) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(field)
                .addConstraintViolation();
        return false;
    }
}