package com.nexo.manada_solidaria_backend.password_recovery.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.password_recovery.data.repositories.PasswordRecoveryRepository;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.NEW_PASSWORD;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.REGISTERED_EMAIL;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.UNREGISTERED_EMAIL;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.extractCode;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.newMimeMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(
        scripts = {"/sql/data-setup.sql", "/sql/users/user-profile-data.sql"},
        statements = "DELETE FROM password_recovery",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class PasswordRecoveryControllerTest extends BaseIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils#";

    @MockitoBean
    private JavaMailSender mailSender;
    @Autowired
    private PasswordRecoveryRepository passwordRecoveryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void stubMailSender() {
        given(mailSender.createMimeMessage()).willAnswer(invocation -> newMimeMessage());
    }

    @DisplayName("Los pedidos mal formados son rechazados")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideInvalidRequestCases")
    void invalidRequestsAreRejected(String testName, String path, Object body) throws Exception {
        perform(path, body).andExpect(status().isBadRequest());
    }

    @DisplayName("Validar el codigo falla")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideVerifyFailureCases")
    void verifyFails(String testName, String email, boolean requestRecoveryFirst) throws Exception {
        String code = "123456";
        if (requestRecoveryFirst) {
            requestRecovery(email);
            code = anyCodeOtherThan(sentCode());
        }

        verifyCode(email, code).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("El flujo completo permite iniciar sesion con la contrasena nueva")
    void fullFlowLetsTheUserLoginWithTheNewPassword() throws Exception {
        requestRecovery(REGISTERED_EMAIL).andExpect(status().isAccepted());

        String resetToken = extractResetToken(
                verifyCode(REGISTERED_EMAIL, sentCode()).andExpect(status().isOk())
        );

        resetPassword(resetToken, NEW_PASSWORD).andExpect(status().isNoContent());

        mockMvc.perform(post("/auth/login").header("Authorization", getCredentials("admin", NEW_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un email sin usuario responde igual pero no envia ningun mail")
    void unknownEmailDoesNotSendAnyMail() throws Exception {
        requestRecovery(UNREGISTERED_EMAIL).andExpect(status().isAccepted());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("El codigo se persiste hasheado, nunca en texto plano")
    void codeIsStoredHashed() throws Exception {
        requestRecovery(REGISTERED_EMAIL);
        String code = sentCode();

        PasswordRecovery recovery = currentRecovery();

        assertThat(recovery.getCodeHash()).isNotEqualTo(code);
        assertThat(passwordEncoder.matches(code, recovery.getCodeHash())).isTrue();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Agotados los intentos, el codigo correcto tampoco sirve")
    void exhaustedAttemptsRejectEvenTheCorrectCode() throws Exception {
        requestRecovery(REGISTERED_EMAIL);
        String code = sentCode();
        String wrongCode = anyCodeOtherThan(code);

        for (int attempt = 0; attempt < 5; attempt++) {
            verifyCode(REGISTERED_EMAIL, wrongCode).andExpect(status().isBadRequest());
        }

        assertThat(currentRecovery().getAttempts()).isEqualTo(5);
        verifyCode(REGISTERED_EMAIL, code).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Un codigo vencido no permite continuar")
    void expiredCodeIsRejected() throws Exception {
        String code = "654321";
        passwordRecoveryRepository.save(new PasswordRecovery(
                registeredUser(),
                passwordEncoder.encode(code),
                LocalDateTime.now().minusMinutes(1)
        ));

        verifyCode(REGISTERED_EMAIL, code).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Un codigo ya validado no se puede volver a usar")
    void verifiedCodeCannotBeReused() throws Exception {
        requestRecovery(REGISTERED_EMAIL);
        String code = sentCode();
        verifyCode(REGISTERED_EMAIL, code).andExpect(status().isOk());

        verifyCode(REGISTERED_EMAIL, code).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("El token de recuperacion no se puede reutilizar")
    void resetTokenCannotBeReused() throws Exception {
        requestRecovery(REGISTERED_EMAIL);
        String resetToken = extractResetToken(verifyCode(REGISTERED_EMAIL, sentCode()));
        resetPassword(resetToken, NEW_PASSWORD).andExpect(status().isNoContent());

        resetPassword(resetToken, "otraPassword456").andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Una segunda solicitud dentro del cooldown no envia otro mail")
    void resendCooldownAvoidsASecondMail() throws Exception {
        requestRecovery(REGISTERED_EMAIL).andExpect(status().isAccepted());
        requestRecovery(REGISTERED_EMAIL).andExpect(status().isAccepted());

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    private ResultActions requestRecovery(String email) throws Exception {
        return perform("/password-recovery/request", new RequestRecoveryRequest(email));
    }

    private ResultActions verifyCode(String email, String code) throws Exception {
        return perform("/password-recovery/verify", new VerifyRecoveryCodeRequest(email, code));
    }

    private ResultActions resetPassword(String resetToken, String password) throws Exception {
        return perform("/password-recovery/reset", new ResetPasswordRequest(resetToken, password, password));
    }

    private ResultActions perform(String path, Object body) throws Exception {
        return mockMvc.perform(
                post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(body))
        );
    }

    private String extractResetToken(ResultActions result) throws Exception {
        return mapper.readTree(result.andReturn().getResponse().getContentAsString())
                .get("resetToken")
                .asText();
    }

    private String sentCode() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, atLeastOnce()).send(captor.capture());
        return extractCode(captor.getValue());
    }

    private PasswordRecovery currentRecovery() {
        return passwordRecoveryRepository
                .findFirstByUserAndUsedAtIsNullOrderByCreatedAtDesc(registeredUser())
                .orElseThrow();
    }

    private User registeredUser() {
        return userRepository.findFirstByProfileEmail(REGISTERED_EMAIL).orElseThrow();
    }

    private static String anyCodeOtherThan(String code) {
        return code.equals("000000") ? "111111" : "000000";
    }
}
