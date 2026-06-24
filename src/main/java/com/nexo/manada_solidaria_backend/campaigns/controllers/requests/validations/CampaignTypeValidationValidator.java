package com.nexo.manada_solidaria_backend.campaigns.controllers.requests.validations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CampaignTypeValidationValidator implements ConstraintValidator<CampaignTypeValidation, CreateCampaignRequest> {

    @Override
    public boolean isValid(CreateCampaignRequest request, ConstraintValidatorContext context
    ) {
        if (request == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        boolean valid = true;

        if (request.type() == CampaignType.NEWS) {

            valid &= requireNull(
                    request.amountToBeCollected(),
                    "amountToBeCollected",
                    "El campo 'amountToBeCollected' no aplica para NEWS",
                    context
            );

            valid &= requireNull(
                    request.campaignEndDate(),
                    "campaignEndDate",
                    "El campo 'campaignEndDate' no aplica para NEWS",
                    context
            );
        }

        return valid;
    }

    private boolean requireNull(Object value, String field, String message, ConstraintValidatorContext context) {
        if (value != null) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}