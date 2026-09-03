package com.nexo.manada_solidaria_backend.common.mail;

import java.util.Map;

public record MailMessage(
        String to,
        String subject,
        String template,
        Map<String, String> variables
) {
}
