package com.nexo.manada_solidaria_backend.common.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PingControlletTest extends BaseAuthenticatedIntegrationTest {

    @DisplayName("Ping tests - Testing authorization logic")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils#providePingTestCases")
    void ping_shouldReturnPong(
            String testName,
            HttpStatus expectedStatus,
            String expectedResponse
    ) throws Exception {
        if (expectedStatus.equals(HttpStatus.UNAUTHORIZED)) {
            accessToken = INVALID_ACCESS_TOKEN;
        }

        mockMvc
                .perform(
                        get("/ping")
                                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(response -> {
                    String responseContent = response.getResponse().getContentAsString();
                    if (expectedStatus.is2xxSuccessful()) {
                        Assertions.assertEquals(expectedResponse, responseContent);
                    }
                });
    }
}
