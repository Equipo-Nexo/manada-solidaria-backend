package com.nexo.manada_solidaria_backend.users.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        String name,

        String lastname,

        @Email
        @NotBlank(message = "Debe ingresar un correo electrónico")
        String email,

        @Valid
        PhoneNumberRequest phoneNumber,

        String profileImageURL
) {
}
