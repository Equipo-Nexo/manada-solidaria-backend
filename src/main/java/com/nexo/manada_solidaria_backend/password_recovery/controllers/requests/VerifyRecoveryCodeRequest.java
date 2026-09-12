package com.nexo.manada_solidaria_backend.password_recovery.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyRecoveryCodeRequest(
        @NotBlank(message = "Debe ingresar un correo electrónico")
        @Email(message = "Debe ingresar un correo electrónico válido")
        String email,

        @NotBlank(message = "Debe ingresar el código de recuperación")
        @Pattern(regexp = "\\d{6}", message = "El código de recuperación debe tener 6 dígitos")
        String code
) {
}
