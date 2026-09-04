package com.nexo.manada_solidaria_backend.password_recovery.components;

import com.nexo.manada_solidaria_backend.password_recovery.components.PasswordRecoveryMailer.PasswordRecoveryInformation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class PasswordRecoveryMailListener {

    private final PasswordRecoveryMailer passwordRecoveryMailer;

    @TransactionalEventListener
    public void onPasswordRecoveryRequested(PasswordRecoveryRequested event) {
        passwordRecoveryMailer.send(event.email(), new PasswordRecoveryInformation(event.code()));
    }

    public record PasswordRecoveryRequested(
            String email,
            String code
    ) {
    }
}
