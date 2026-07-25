package com.nexo.manada_solidaria_backend.users.controllers.requests;

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
                regexp = "^\\+?[1-9]\\d{7,14}$",
                message = "El número de teléfono no tiene un formato válido"
        )
        String phoneNumber,

        String profileImageURL
) {
}
