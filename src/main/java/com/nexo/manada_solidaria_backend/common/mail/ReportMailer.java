package com.nexo.manada_solidaria_backend.common.mail;

import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReportMailer extends Mailer<ReportMailer.ReportInformation> {

    private static final String SUBJECT = "⚠️ Publicación reportada para revisión";
    private static final String TEMPLATE = "report-post";

    public record ReportInformation(
            String postUrl
    ) {
    }

    public ReportMailer(
            JavaMailSender javaMailSender,
            MailProperties mailProperties
    ) {
        super(javaMailSender, mailProperties);
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
    protected Map<String, String> variables(ReportInformation information) {
        return Map.of(
                "postUrl", information.postUrl()
        );
    }
}