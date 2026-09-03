package com.nexo.manada_solidaria_backend.password_recovery.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.password_recovery.data.repositories.PasswordRecoveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Comparator;
import java.util.List;

import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.NEW_PASSWORD;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.REGISTERED_EMAIL;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.extractCode;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.newMimeMessage;
import static com.nexo.manada_solidaria_backend.password_recovery.data.enums.PasswordRecoveryStatus.REVOKED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(
        scripts = {"/sql/data-setup.sql", "/sql/users/user-profile-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@TestPropertySource(properties = "security.password-recovery.resend-cooldown=0")
class PasswordRecoveryWithoutCooldownTest extends BaseIntegrationTest {

    @MockitoBean
    private JavaMailSender mailSender;
    @Autowired
    private PasswordRecoveryRepository passwordRecoveryRepository;

    @BeforeEach
    void stubMailSender() {
        given(mailSender.createMimeMessage()).willAnswer(invocation -> newMimeMessage());
    }

    @Test
    @DisplayName("Una solicitud nueva invalida el codigo anterior")
    void aNewRequestInvalidatesThePreviousCode() throws Exception {
        requestRecovery().andExpect(status().isAccepted());
        requestRecovery().andExpect(status().isAccepted());

        List<String> codes = sentCodes();
        assertThat(codes).hasSize(2);

        PasswordRecovery previous = passwordRecoveryRepository.findAll().stream()
                .min(Comparator.comparing(PasswordRecovery::getCreatedAt))
                .orElseThrow();
        assertThat(previous.getStatus()).isEqualTo(REVOKED);
        assertThat(previous.getUsedAt()).isNull();

        verifyCode(codes.getFirst()).andExpect(status().isBadRequest());
        verifyCode(codes.getLast()).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Una solicitud nueva tambien invalida un codigo ya verificado")
    void aNewRequestInvalidatesAnAlreadyVerifiedCode() throws Exception {
        requestRecovery();
        String resetToken = extractResetToken(verifyCode(lastSentCode()).andExpect(status().isOk()));

        requestRecovery().andExpect(status().isAccepted());

        resetPassword(resetToken).andExpect(status().isBadRequest());
    }

    private ResultActions requestRecovery() throws Exception {
        return perform("/password-recovery/request", new RequestRecoveryRequest(REGISTERED_EMAIL));
    }

    private ResultActions verifyCode(String code) throws Exception {
        return perform("/password-recovery/verify", new VerifyRecoveryCodeRequest(REGISTERED_EMAIL, code));
    }

    private ResultActions resetPassword(String resetToken) throws Exception {
        return perform(
                "/password-recovery/reset",
                new ResetPasswordRequest(resetToken, NEW_PASSWORD, NEW_PASSWORD)
        );
    }

    private String extractResetToken(ResultActions result) throws Exception {
        return mapper.readTree(result.andReturn().getResponse().getContentAsString())
                .get("resetToken")
                .asText();
    }

    private String lastSentCode() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, atLeastOnce()).send(captor.capture());
        return extractCode(captor.getValue());
    }

    private ResultActions perform(String path, Object body) throws Exception {
        return mockMvc.perform(
                post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(body))
        );
    }

    private List<String> sentCodes() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(2)).send(captor.capture());
        return captor.getAllValues().stream()
                .map(message -> extractCode(message))
                .toList();
    }
}
