package com.nexo.manada_solidaria_backend.password_recovery.components;

import com.nexo.manada_solidaria_backend.password_recovery.config.PasswordRecoveryProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PasswordRecoveryMailer {

    private static final String SUBJECT = "Tu código para recuperar la contraseña";
    private static final String LOGO_CONTENT_ID = "logo";
    private static final Resource LOGO = new ClassPathResource("mail/manada-solidaria-logo.png");
    private static final String CODE_PLACEHOLDER = "{{code}}";
    private static final String MINUTES_PLACEHOLDER = "{{minutes}}";

    private final JavaMailSender mailSender;
    private final String sender;
    private final String htmlTemplate;
    private final String textTemplate;
    private final long codeExpiration;

    public PasswordRecoveryMailer(
            JavaMailSender mailSender,
            PasswordRecoveryProperties properties,
            @Value("${spring.mail.username}") String sender
    ) {
        this.mailSender = mailSender;
        this.sender = sender;
        this.codeExpiration = properties.getCodeExpiration();
        this.htmlTemplate = readTemplate("mail/password-recovery.html");
        this.textTemplate = readTemplate("mail/password-recovery.txt");
    }

    public void sendRecoveryCode(String email, String code) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
            );
            helper.setFrom(sender);
            helper.setTo(email);
            helper.setSubject(SUBJECT);
            helper.setText(fill(textTemplate, code), fill(htmlTemplate, code));
            helper.addInline(LOGO_CONTENT_ID, LOGO);
        } catch (MessagingException exception) {
            throw new MailParseException(exception);
        }
        mailSender.send(message);
    }

    private String fill(String template, String code) {
        return template
                .replace(CODE_PLACEHOLDER, code)
                .replace(MINUTES_PLACEHOLDER, String.valueOf(codeExpiration));
    }

    private static String readTemplate(String path) {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer la plantilla " + path, exception);
        }
    }
}
