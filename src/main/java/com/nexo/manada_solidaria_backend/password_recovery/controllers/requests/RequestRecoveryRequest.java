package com.nexo.manada_solidaria_backend.password_recovery.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestRecoveryRequest(
        @NotBlank(message = "Debe ingresar un correo electrónico")
        @Email(message = "Debe ingresar un correo electrónico válido")
        String email
) {
}
