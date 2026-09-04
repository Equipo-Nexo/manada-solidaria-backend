package com.nexo.manada_solidaria_backend.password_recovery.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.NEW_PASSWORD;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.REGISTERED_EMAIL;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.extractCode;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.newMimeMessage;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(
        scripts = {"/sql/data-setup.sql", "/sql/users/user-profile-data.sql"},
        statements = "DELETE FROM password_recovery",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@TestPropertySource(properties = "security.password-recovery.token-expiration=0")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PasswordRecoveryTokenExpirationTest extends BaseIntegrationTest {

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void stubMailSender() {
        given(mailSender.createMimeMessage()).willAnswer(invocation -> newMimeMessage());
    }

    @Test
    @DisplayName("Un token de recuperacion vencido no permite cambiar la contrasena")
    void expiredResetTokenIsRejected() throws Exception {
        perform("/password-recovery/request", new RequestRecoveryRequest(REGISTERED_EMAIL));

        String resetToken = extractResetToken(
                perform("/password-recovery/verify", new VerifyRecoveryCodeRequest(REGISTERED_EMAIL, sentCode()))
                        .andExpect(status().isOk())
        );

        perform("/password-recovery/reset", new ResetPasswordRequest(resetToken, NEW_PASSWORD, NEW_PASSWORD))
                .andExpect(status().isBadRequest());
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
}
