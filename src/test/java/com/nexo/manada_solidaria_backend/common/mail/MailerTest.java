package com.nexo.manada_solidaria_backend.common.mail;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MailerTest {

    private static final String TEMPLATE_WITH_TWO_VARIABLES = "password-recovery";
    private static final JavaMailSenderImpl MESSAGE_FACTORY = new JavaMailSenderImpl();

    private JavaMailSender javaMailSender;
    private TestMailer mailer;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        given(javaMailSender.createMimeMessage()).willAnswer(invocation -> MESSAGE_FACTORY.createMimeMessage());

        MailProperties mailProperties = new MailProperties();
        mailProperties.setUsername("manada-solidaria@test.local");
        mailer = new TestMailer(javaMailSender, mailProperties);
    }

    @DisplayName("El mail sale solo si la plantilla quedo sin variables pendientes")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideTemplateVariableCases")
    void templateVariables(String testName, Map<String, String> variables, int expectedSends) {
        mailer.send("destinatario@mail.com", variables);

        verify(javaMailSender, times(expectedSends)).send(any(MimeMessage.class));
    }

    private static Stream<Arguments> provideTemplateVariableCases() {
        return Stream.of(
                Arguments.of(
                        "Con todas las variables de la plantilla",
                        Map.of("code", "123456", "minutes", "10"),
                        1
                ),
                Arguments.of(
                        "Sin una de las variables de la plantilla",
                        Map.of("code", "123456"),
                        0
                ),
                Arguments.of(
                        "Sin ninguna variable",
                        Map.of(),
                        0
                )
        );
    }

    private static class TestMailer extends Mailer<Map<String, String>> {

        TestMailer(JavaMailSender javaMailSender, MailProperties mailProperties) {
            super(javaMailSender, mailProperties);
        }

        @Override
        protected String subject() {
            return "Asunto de prueba";
        }

        @Override
        protected String template() {
            return TEMPLATE_WITH_TWO_VARIABLES;
        }

        @Override
        protected Map<String, String> variables(Map<String, String> information) {
            return information;
        }
    }
}
