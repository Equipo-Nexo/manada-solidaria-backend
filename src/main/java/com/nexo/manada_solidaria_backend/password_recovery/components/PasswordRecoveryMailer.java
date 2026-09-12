package com.nexo.manada_solidaria_backend.password_recovery.components;

import com.nexo.manada_solidaria_backend.common.mail.Mailer;
import com.nexo.manada_solidaria_backend.password_recovery.config.PasswordRecoveryProperties;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PasswordRecoveryMailer extends Mailer<PasswordRecoveryMailer.PasswordRecoveryInformation> {

    private static final String SUBJECT = "Tu código para recuperar la contraseña";
    private static final String TEMPLATE = "password-recovery";

    public record PasswordRecoveryInformation(
            String code
    ) {
    }

    private final PasswordRecoveryProperties properties;

    public PasswordRecoveryMailer(
            JavaMailSender javaMailSender,
            MailProperties mailProperties,
            PasswordRecoveryProperties properties
    ) {
        super(javaMailSender, mailProperties);
        this.properties = properties;
    }

    @Override
    protected String subject() {
        return SUBJECT;
    }

    @Override
    protected String template() {
        return TEMPLATE;
    }

    @Override
    protected Map<String, String> variables(PasswordRecoveryInformation information) {
        return Map.of(
                "code", information.code(),
                "minutes", String.valueOf(properties.codeExpiration())
        );
    }
}
