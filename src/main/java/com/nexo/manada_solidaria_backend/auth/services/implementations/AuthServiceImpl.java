package com.nexo.manada_solidaria_backend.auth.services.implementations;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.UserSessionResponse;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.AuthService;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import io.jsonwebtoken.ExpiredJwtException;
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
    public ResponseEntity<UserSessionResponse> login(String username) {
        User user = userService.loadUserByUsername(username);
        return buildUserSessionResponse(user);
    }

    @Override
    public void signup(CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }

    @Override
    public ResponseEntity<UserSessionResponse> refreshSession(String refreshToken) {
        try {
            User user = userService.getUserById(
                    UUID.fromString(jsonWebTokenService.getSubject(refreshToken))
            );
            return buildUserSessionResponse(user);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is not valid");
        }
    }

    private static Map<String, Object> getCustomClaims(User user) {
        return Map.of(ROLES_CLAIM_NAME, user.getAuthorities());
    }

    private static String getUserId(User user) {
        return user.getId().toString();
    }

    private ResponseEntity<UserSessionResponse> buildUserSessionResponse(User user) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", getRefreshToken(user))
                .httpOnly(true)
                .path("/auth")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new UserSessionResponse(
                        user.getId(),
                        getAccessToken(user)
                ));
    }

    private String getAccessToken(User user) {
        return jsonWebTokenService.getAccessToken(getUserId(user), getCustomClaims(user));
    }

    private String getRefreshToken(User user) {
        return jsonWebTokenService.getRefreshToken(getUserId(user), Collections.emptyMap());
    }
}
