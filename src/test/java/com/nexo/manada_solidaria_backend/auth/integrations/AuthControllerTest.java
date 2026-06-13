package com.nexo.manada_solidaria_backend.auth.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.auth.utils.MockAuthDataUtils#provideLoginTestCases")
    void loginTests(
            String testName,
            String username,
            String password,
            HttpStatus expectedStatus
    ) throws Exception {
        mockMvc
                .perform(
                        post("/auth/login")
                                .header("Authorization", getCredentials(username, password))
                )
                .andExpect(status().is(expectedStatus.value()))
                .andExpectAll(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    if (expectedStatus.is2xxSuccessful()) {
                        Assertions.assertNotNull(responseContent);
                        Assertions.assertTrue(responseContent.contains("accessToken"));
                    }
                });
    }

    private static String getCredentials(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
