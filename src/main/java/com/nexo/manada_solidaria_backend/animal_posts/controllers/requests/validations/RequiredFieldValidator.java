package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class RequiredFieldValidator implements ConstraintValidator<RequiredField, Object> {

    private String field;
    private String dependsOn;
    private String expectedValue;

    @Override
    public void initialize(RequiredField annotation) {
        ConstraintValidator.super.initialize(annotation);
        this.field = annotation.field();
        this.dependsOn = annotation.dependsOn();
        this.expectedValue = annotation.expectedValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);

        Object dependsOnValue = wrapper.getPropertyValue(dependsOn);
        Object fieldValue = wrapper.getPropertyValue(field);

        if (dependsOnValue != null &&
                expectedValue.equals(dependsOnValue.toString()) &&
                fieldValue == null) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(field)
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
