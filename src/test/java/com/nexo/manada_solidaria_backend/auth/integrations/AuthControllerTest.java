package com.nexo.manada_solidaria_backend.auth.integrations;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.auth.utils.MockAuthDataUtils#";

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Login tests")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideLoginTestCases")
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

    @DisplayName("Los pedidos de registro invalidos son rechazados")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideInvalidSignupCases")
    void signupRejectsInvalidRequests(
            String testName,
            CreateUserRequest request,
            Matcher<?> expectedError
    ) throws Exception {
        signup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", expectedError));
    }

    @DisplayName("El registro persiste el usuario con sus roles")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideSignupCases")
    void signupCreatesTheUser(
            String testName,
            CreateUserRequest request,
            List<Rol> expectedRoles
    ) throws Exception {
        signup(request).andExpect(status().isCreated());

        User created = userRepository.findByUsername(request.getUsername()).orElseThrow();
        assertThat(created.getProfile().getRoles()).containsExactlyInAnyOrderElementsOf(expectedRoles);
    }

    private ResultActions signup(CreateUserRequest request) throws Exception {
        return mockMvc.perform(
                post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                        .accept(MediaType.APPLICATION_JSON)
        );
    }
}
