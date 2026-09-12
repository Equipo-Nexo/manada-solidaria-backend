package com.nexo.manada_solidaria_backend.password_recovery.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.validations.PasswordConfirmation;
import com.nexo.manada_solidaria_backend.common.controllers.validations.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record ResetPasswordRequest(
        @NotBlank(message = "Debe ingresar el token de recuperación")
        String resetToken,

        @NotBlank(message = "Debe ingresar una contraseña")
        @Size(min = 6, message = "la contraseña debe tener al menos 6 caracteres")
        String newPassword,

        @NotBlank(message = "Debe repetir la contraseña")
        String newPasswordVerification
) implements PasswordConfirmation {

    @Override
    public String getPassword() {
        return newPassword;
    }

    @Override
    public String getRepeatedPassword() {
        return newPasswordVerification;
    }
}
