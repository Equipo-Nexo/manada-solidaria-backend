package com.nexo.manada_solidaria_backend.users.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.validations.PhoneValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        String name,

        String lastname,

        @Email
        @NotBlank(message = "Debe ingresar un correo electrónico")
        String email,

        @Pattern(
                regexp = PhoneValidation.AREA_CODE_REGEX,
                message = PhoneValidation.AREA_CODE_MESSAGE
        )
        String areaCode,

        @Pattern(
                regexp = PhoneValidation.PHONE_NUMBER_REGEX,
                message = PhoneValidation.PHONE_NUMBER_MESSAGE
        )
        String phoneNumber,

        String profileImageURL
) {
}
