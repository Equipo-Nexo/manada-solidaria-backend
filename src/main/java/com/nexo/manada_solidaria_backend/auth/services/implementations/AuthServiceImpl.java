package com.nexo.manada_solidaria_backend.auth.services.implementations;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.LoginResponse;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.AuthService;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.auth.utils.SecurityConstants.ROLES_CLAIM_NAME;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JsonWebTokenService jsonWebTokenService;
    private final UserService userService;

    @Override
    public ResponseEntity<LoginResponse> login(String username) {
        User user = userService.loadUserByUsername(username);
        return buildLoginResponse(user);
    }

    @Override
    public void signup(CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }

    @Override
    public ResponseEntity<LoginResponse> refreshSession(String refreshToken) {
        if (refreshTokenIsValid(refreshToken)) {
            User user = userService.getUserById(
                    UUID.fromString(jsonWebTokenService.getSubject(refreshToken))
            );
            return buildLoginResponse(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is not valid");
        }
    }

    private boolean refreshTokenIsValid(String refreshToken) {
        return jsonWebTokenService.isValid(refreshToken);
    }

    private static Map<String, Object> getCustomClaims(User user) {
        return Map.of(ROLES_CLAIM_NAME, user.getAuthorities());
    }

    private static String getUserId(User user) {
        return user.getId().toString();
    }

    private ResponseEntity<LoginResponse> buildLoginResponse(User user) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", getRefreshToken(user))
                .httpOnly(true)
                .path("/auth")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(
                        user.getId(),
                        jsonWebTokenService.getAccessToken(getUserId(user), getCustomClaims(user))
                ));
    }

    private String getRefreshToken(User user) {
        return jsonWebTokenService.getRefreshToken(getUserId(user), Collections.emptyMap());
    }
}
