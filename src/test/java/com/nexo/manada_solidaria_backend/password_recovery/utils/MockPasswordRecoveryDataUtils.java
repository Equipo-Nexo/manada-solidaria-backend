package com.nexo.manada_solidaria_backend.password_recovery.utils;

import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

public class MockPasswordRecoveryDataUtils {

    public static final String REGISTERED_EMAIL = "admin@mail.com";
    public static final String UNREGISTERED_EMAIL = "nadie@mail.com";
    public static final String NEW_PASSWORD = "NuevaPassword123!";

    private static final String OVER_SEVENTY_TWO_BYTES = "Aa1!" + "ñ".repeat(39);

    private static final Pattern CODE_PATTERN = Pattern.compile("\\d{6}");
    private static final JavaMailSenderImpl MESSAGE_FACTORY = new JavaMailSenderImpl();

    public static MimeMessage newMimeMessage() {
        return MESSAGE_FACTORY.createMimeMessage();
    }

    public static String extractCode(MimeMessage message) {
        Matcher matcher = CODE_PATTERN.matcher(plainTextOf(message));
        if (!matcher.find()) {
            throw new IllegalStateException("El mail no contiene un código de 6 dígitos");
        }
        return matcher.group();
    }

    private static String plainTextOf(MimeMessage message) {
        StringBuilder body = new StringBuilder();
        appendPlainText(message, body);
        return body.toString();
    }

    private static void appendPlainText(Part part, StringBuilder body) {
        try {
            Object content = part.getContent();
            if (content instanceof Multipart multipart) {
                for (int index = 0; index < multipart.getCount(); index++) {
                    appendPlainText(multipart.getBodyPart(index), body);
                }
                return;
            }
            if (part.isMimeType("text/plain")) {
                body.append(content);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo leer el cuerpo del mail", exception);
        }
    }

    private static Stream<Arguments> provideInvalidRequestCases() {
        return Stream.of(
                Arguments.of(
                        "Solicitar con un email mal formado",
                        "/password-recovery/request",
                        new RequestRecoveryRequest("no-es-un-email"),
                        hasItem(containsString("Debe ingresar un correo"))
                ),
                Arguments.of(
                        "Solicitar sin email",
                        "/password-recovery/request",
                        new RequestRecoveryRequest(null),
                        hasItem(containsString("Debe ingresar un correo"))
                ),
                Arguments.of(
                        "Validar con un codigo de menos de 6 digitos",
                        "/password-recovery/verify",
                        new VerifyRecoveryCodeRequest(REGISTERED_EMAIL, "12345"),
                        hasItem(containsString("debe tener 6"))
                ),
                Arguments.of(
                        "Validar con un codigo que no es numerico",
                        "/password-recovery/verify",
                        new VerifyRecoveryCodeRequest(REGISTERED_EMAIL, "abcdef"),
                        hasItem(containsString("debe tener 6"))
                ),
                Arguments.of(
                        "Validar sin codigo",
                        "/password-recovery/verify",
                        new VerifyRecoveryCodeRequest(REGISTERED_EMAIL, null),
                        hasItem(containsString("Debe ingresar el c"))
                ),
                Arguments.of(
                        "Restablecer con una contraseña de menos de 8 caracteres",
                        "/password-recovery/reset",
                        new ResetPasswordRequest("un-token", "Aa1!sss", "Aa1!sss"),
                        hasItem(containsString("8 caracteres"))
                ),
                Arguments.of(
                        "Restablecer con una contraseña sin caracter especial",
                        "/password-recovery/reset",
                        new ResetPasswordRequest("un-token", "Aa11ssss", "Aa11ssss"),
                        hasItem(containsString("cter especial"))
                ),
                Arguments.of(
                        "Restablecer con una contraseña que supera los 72 bytes",
                        "/password-recovery/reset",
                        new ResetPasswordRequest("un-token", OVER_SEVENTY_TWO_BYTES, OVER_SEVENTY_TWO_BYTES),
                        hasItem(containsString("no puede superar los 64 caracteres"))
                ),
                Arguments.of(
                        "Restablecer con contraseñas que no coinciden",
                        "/password-recovery/reset",
                        new ResetPasswordRequest("un-token", NEW_PASSWORD, "otraPassword123"),
                        hasItem(containsString("no coinciden"))
                ),
                Arguments.of(
                        "Restablecer sin token",
                        "/password-recovery/reset",
                        new ResetPasswordRequest(null, NEW_PASSWORD, NEW_PASSWORD),
                        hasItem(containsString("Debe ingresar el token"))
                )
        );
    }

    private static Stream<Arguments> provideVerifyFailureCases() {
        return Stream.of(
                Arguments.of("Sin una solicitud vigente", REGISTERED_EMAIL, RecoverySetup.NONE),
                Arguments.of("Con un codigo incorrecto", REGISTERED_EMAIL, RecoverySetup.WRONG_CODE),
                Arguments.of("Con un codigo vencido", REGISTERED_EMAIL, RecoverySetup.EXPIRED_CODE),
                Arguments.of("Con un email sin usuario", UNREGISTERED_EMAIL, RecoverySetup.NONE)
        );
    }

    public enum RecoverySetup {
        NONE,
        WRONG_CODE,
        EXPIRED_CODE
    }
}
