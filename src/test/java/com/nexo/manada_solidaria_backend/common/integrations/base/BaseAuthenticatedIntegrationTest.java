package com.nexo.manada_solidaria_backend.common.integrations.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Sql(
        scripts = "/data-setup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public abstract class BaseAuthenticatedIntegrationTest extends BaseIntegrationTest {

    protected String accessToken;

    @BeforeEach
    void authenticate() throws Exception {
        String response = mockMvc.perform(
                post("/auth/login")
                        .header("Authorization", getCredentials("admin", "admin"))
        ).andReturn().getResponse().getContentAsString();

        accessToken = mapper.readTree(response).get("accessToken").asText();
    }

}
