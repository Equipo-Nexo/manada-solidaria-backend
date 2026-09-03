package com.nexo.manada_solidaria_backend.common.mail;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Mailer {

    private static final String SENDER_NAME = "Manada Solidaria";
    private static final String TEMPLATES_PATH = "mail/";
    private static final String HTML_EXTENSION = ".html";
    private static final String TEXT_EXTENSION = ".txt";
    private static final String LOGO_PATH = "mail/manada-solidaria-logo.png";
    private static final String LOGO_CONTENT_ID = "logo";
    private static final String LOGO_CONTENT_TYPE = "image/png";
    private static final String LOGO_NAME = "logo";
    private static final Pattern PENDING_VARIABLE = Pattern.compile("\\{\\{(\\w+)}}");

    private final JavaMailSender javaMailSender;
    private final String sender;
    private final DataSource logo;
    private final Map<String, String> templates = new ConcurrentHashMap<>();

    public Mailer(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String sender) {
        this.javaMailSender = javaMailSender;
        this.sender = sender;
        this.logo = new ByteArrayDataSource(read(LOGO_PATH), LOGO_CONTENT_TYPE);
    }

    public void send(MailMessage message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name()
            );
            helper.setFrom(sender, SENDER_NAME);
            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(render(message, TEXT_EXTENSION), render(message, HTML_EXTENSION));
            helper.getMimeMultipart().addBodyPart(buildLogoPart());
        } catch (MessagingException | UnsupportedEncodingException exception) {
            throw new MailParseException(exception);
        }
        javaMailSender.send(mimeMessage);
    }

    private String render(MailMessage message, String extension) {
        String content = template(message.template() + extension);
        for (Map.Entry<String, String> variable : message.variables().entrySet()) {
            content = content.replace("{{" + variable.getKey() + "}}", variable.getValue());
        }

        Matcher pending = PENDING_VARIABLE.matcher(content);
        if (pending.find()) {
            throw new MailPreparationException(
                    "Falta la variable " + pending.group(1) + " en la plantilla " + message.template()
            );
        }
        return content;
    }

    private String template(String name) {
        return templates.computeIfAbsent(
                name,
                path -> new String(read(TEMPLATES_PATH + path), StandardCharsets.UTF_8)
        );
    }

    private MimeBodyPart buildLogoPart() throws MessagingException {
        MimeBodyPart logoPart = new MimeBodyPart();
        logoPart.setDataHandler(new DataHandler(logo));
        logoPart.setContentID("<" + LOGO_CONTENT_ID + ">");
        logoPart.setDisposition(MimeBodyPart.INLINE);
        logoPart.setFileName(LOGO_NAME);
        return logoPart;
    }

    private static byte[] read(String path) {
        try {
            return new ClassPathResource(path).getContentAsByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer el recurso " + path, exception);
        }
    }
}
