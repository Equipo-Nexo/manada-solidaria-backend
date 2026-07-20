package com.nexo.manada_solidaria_backend.common.controllers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.DirectFieldAccessor;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;
    private boolean allowNullStart;

    @Override
    public void initialize(DateRange annotation) {
        this.startField = annotation.startField();
        this.endField = annotation.endField();
        this.allowNullStart = annotation.allowNullStart();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        DirectFieldAccessor accessor = new DirectFieldAccessor(value);

        Object start = accessor.getPropertyValue(startField);
        Object end = accessor.getPropertyValue(endField);

        if (end == null) {
            return true;
        }

        if (start == null) {
            return allowNullStart;
        }

        if (!start.getClass().equals(end.getClass())) {
            throw new IllegalArgumentException(
                    "Los campos '" + startField + "' y '" + endField + "' deben ser del mismo tipo."
            );
        }

        if (!(start instanceof Comparable<?>)) {
            throw new IllegalArgumentException(
                    "El campo '" + startField + "' debe implementar Comparable."
            );
        }

        boolean valid = ((Comparable<Object>) end).compareTo(start) > 0;

        if (valid) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(endField)
                .addConstraintViolation();

        return false;
    }
}