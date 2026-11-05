package com.nexo.manada_solidaria_backend.password_recovery.services.interfaces;

import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.responses.RecoveryTokenResponse;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface PasswordRecoveryService {

    void requestRecovery(RequestRecoveryRequest request);

    RecoveryTokenResponse verifyCode(VerifyRecoveryCodeRequest request);

    void resetPassword(ResetPasswordRequest request);

    PasswordRecovery getTotp(User user);
}
