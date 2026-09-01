package com.nexo.manada_solidaria_backend.password_recovery.components;

import com.nexo.manada_solidaria_backend.password_recovery.config.PasswordRecoveryProperties;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class PasswordRecoveryMailer {

    private static final String SUBJECT = "Tu código para recuperar la contraseña";
    private static final String SENDER_NAME = "Manada Solidaria";
    private static final String LOGO_CONTENT_ID = "logo";
    private static final String LOGO_CONTENT_TYPE = "image/png";
    private static final String LOGO_NAME = "logo";
    private static final String CODE_PLACEHOLDER = "{{code}}";
    private static final String MINUTES_PLACEHOLDER = "{{minutes}}";

    private final JavaMailSender mailSender;
    private final String sender;
    private final String htmlTemplate;
    private final String textTemplate;
    private final DataSource logo;
    private final long codeExpiration;

    public PasswordRecoveryMailer(
            JavaMailSender mailSender,
            PasswordRecoveryProperties properties,
            @Value("${spring.mail.username}") String sender
    ) {
        this.mailSender = mailSender;
        this.sender = sender;
        this.codeExpiration = properties.getCodeExpiration();
        this.htmlTemplate = new String(read("mail/password-recovery.html"), StandardCharsets.UTF_8);
        this.textTemplate = new String(read("mail/password-recovery.txt"), StandardCharsets.UTF_8);
        this.logo = new ByteArrayDataSource(read("mail/manada-solidaria-logo.png"), LOGO_CONTENT_TYPE);
    }

    public void sendRecoveryCode(String email, String code) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name()
            );
            helper.setFrom(sender, SENDER_NAME);
            helper.setTo(email);
            helper.setSubject(SUBJECT);
            helper.setText(fill(textTemplate, code), fill(htmlTemplate, code));
            helper.getMimeMultipart().addBodyPart(buildLogoPart());
        } catch (MessagingException | UnsupportedEncodingException exception) {
            throw new MailParseException(exception);
        }
        mailSender.send(message);
    }

    private MimeBodyPart buildLogoPart() throws MessagingException {
        MimeBodyPart logoPart = new MimeBodyPart();
        logoPart.setDataHandler(new DataHandler(logo));
        logoPart.setContentID("<" + LOGO_CONTENT_ID + ">");
        logoPart.setDisposition(MimeBodyPart.INLINE);
        logoPart.setFileName(LOGO_NAME);
        return logoPart;
    }

    private String fill(String template, String code) {
        return template
                .replace(CODE_PLACEHOLDER, code)
                .replace(MINUTES_PLACEHOLDER, String.valueOf(codeExpiration));
    }

    private static byte[] read(String path) {
        try {
            return new ClassPathResource(path).getContentAsByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer el recurso " + path, exception);
        }
    }
}
