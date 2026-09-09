package com.nexo.manada_solidaria_backend.password_recovery.controllers.implementations;

import com.nexo.manada_solidaria_backend.password_recovery.controllers.interfaces.PasswordRecoveryController;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.responses.RecoveryTokenResponse;
import com.nexo.manada_solidaria_backend.password_recovery.services.interfaces.PasswordRecoveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PasswordRecoveryControllerImpl implements PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @Override
    public void requestRecovery(RequestRecoveryRequest request) {
        passwordRecoveryService.requestRecovery(request);
    }

    @Override
    public RecoveryTokenResponse verifyCode(VerifyRecoveryCodeRequest request) {
        return passwordRecoveryService.verifyCode(request);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        passwordRecoveryService.resetPassword(request);
    }
}
