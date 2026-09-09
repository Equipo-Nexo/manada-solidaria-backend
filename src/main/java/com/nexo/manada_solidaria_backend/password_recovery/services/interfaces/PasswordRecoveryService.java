package com.nexo.manada_solidaria_backend.password_recovery.services.interfaces;

import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.responses.RecoveryTokenResponse;

public interface PasswordRecoveryService {

    void requestRecovery(RequestRecoveryRequest request);

    RecoveryTokenResponse verifyCode(VerifyRecoveryCodeRequest request);

    void resetPassword(ResetPasswordRequest request);
}
