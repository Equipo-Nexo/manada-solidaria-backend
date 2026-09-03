package com.nexo.manada_solidaria_backend.password_recovery.components;

import com.nexo.manada_solidaria_backend.common.mail.MailMessage;
import com.nexo.manada_solidaria_backend.common.mail.Mailer;
import com.nexo.manada_solidaria_backend.password_recovery.config.PasswordRecoveryProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class PasswordRecoveryMailer {

    private static final String SUBJECT = "Tu código para recuperar la contraseña";
    private static final String TEMPLATE = "password-recovery";

    private final Mailer mailer;
    private final PasswordRecoveryProperties properties;

    public void sendRecoveryCode(String email, String code) {
        mailer.send(new MailMessage(
                email,
                SUBJECT,
                TEMPLATE,
                Map.of(
                        "code", code,
                        "minutes", String.valueOf(properties.getCodeExpiration())
                )
        ));
    }
}
