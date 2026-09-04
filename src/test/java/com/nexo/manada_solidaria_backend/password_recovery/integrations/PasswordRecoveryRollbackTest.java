package com.nexo.manada_solidaria_backend.password_recovery.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.REGISTERED_EMAIL;
import static com.nexo.manada_solidaria_backend.password_recovery.utils.MockPasswordRecoveryDataUtils.newMimeMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(
        scripts = {"/sql/data-setup.sql", "/sql/users/user-profile-data.sql"},
        statements = "DELETE FROM password_recovery",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class PasswordRecoveryRollbackTest extends BaseIntegrationTest {

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void stubMailSender() {
        given(mailSender.createMimeMessage()).willAnswer(invocation -> newMimeMessage());
    }

    @Test
    @DisplayName("Si la transaccion no commitea, el codigo no se envia")
    void noMailIsSentWhenTheTransactionDoesNotCommit() throws Exception {
        mockMvc.perform(
                post("/password-recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new RequestRecoveryRequest(REGISTERED_EMAIL)))
        ).andExpect(status().isAccepted());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
