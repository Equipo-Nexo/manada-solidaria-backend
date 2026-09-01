package com.nexo.manada_solidaria_backend.password_recovery.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.REGISTERED_EMAIL;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.extractCode;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.newMimeMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
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

    private static final Pattern CODE_PATTERN = Pattern.compile("\\d{6}");

    @MockitoBean
    private JavaMailSender mailSender;

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

        verifyCode(codes.getFirst()).andExpect(status().isBadRequest());
        verifyCode(codes.getLast()).andExpect(status().isOk());
    }

    private ResultActions requestRecovery() throws Exception {
        return perform("/password-recovery/request", new RequestRecoveryRequest(REGISTERED_EMAIL));
    }

    private ResultActions verifyCode(String code) throws Exception {
        return perform("/password-recovery/verify", new VerifyRecoveryCodeRequest(REGISTERED_EMAIL, code));
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
