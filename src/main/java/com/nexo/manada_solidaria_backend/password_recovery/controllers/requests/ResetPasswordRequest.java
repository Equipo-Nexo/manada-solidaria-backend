package com.nexo.manada_solidaria_backend.password_recovery.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.validations.PasswordConfirmation;
import com.nexo.manada_solidaria_backend.common.controllers.validations.PasswordMatches;
import com.nexo.manada_solidaria_backend.common.controllers.validations.SecurePassword;
import jakarta.validation.constraints.NotBlank;

@PasswordMatches
public record ResetPasswordRequest(
        @NotBlank(message = "Debe ingresar el token de recuperación")
        String resetToken,

        @SecurePassword
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
