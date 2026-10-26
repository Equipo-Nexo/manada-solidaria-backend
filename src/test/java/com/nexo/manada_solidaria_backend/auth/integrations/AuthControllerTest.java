package com.nexo.manada_solidaria_backend.auth.integrations;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.auth.utils.MockAuthDataUtils#";
    private static final String USER_ID = "22222222-2222-2222-2222-222222222222";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JsonWebTokenService jsonWebTokenService;
    @Value("${security.secret}")
    private String jwtSecret;

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

    @DisplayName("Refresh user session")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideRefreshSessionTestCases")
    void refreshSession(
            String testName,
            boolean validRefreshToken,
            HttpStatus expectedStatus
    ) throws Exception {
        String refreshToken = validRefreshToken
                ? jsonWebTokenService.getRefreshToken(USER_ID, Collections.emptyMap())
                : getExpiredRefreshToken();

        mockMvc
                .perform(
                        post("/auth/refresh")
                                .cookie(new Cookie("refreshToken", refreshToken))
                )
                .andExpect(status().is(expectedStatus.value()))
                .andExpectAll(result -> {
                    if (expectedStatus.is2xxSuccessful()) {
                        jsonPath("$.userId").value(USER_ID).match(result);
                        jsonPath("$.accessToken").isNotEmpty().match(result);
                        Assertions.assertTrue(
                                result.getResponse().getHeader(HttpHeaders.SET_COOKIE).contains("refreshToken=")
                        );
                    } else {
                        jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()).match(result);
                        jsonPath("$.errors[0]").value("Refresh token is not valid").match(result);
                        jsonPath("$.path").value("/auth/refresh").match(result);
                    }
                });
    }

    private String getExpiredRefreshToken() {
        Instant issuedAt = Instant.now().minus(2, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(USER_ID)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(1, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();
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
