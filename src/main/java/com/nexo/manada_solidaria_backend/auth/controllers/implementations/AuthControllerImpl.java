package com.nexo.manada_solidaria_backend.auth.controllers.implementations;

import com.nexo.manada_solidaria_backend.auth.controllers.interfaces.AuthController;
import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.UserSessionResponse;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.AuthService;
import com.nexo.manada_solidaria_backend.auth.utils.BasicAuthUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<UserSessionResponse> login(String credentials) {
        return authService.login(
                BasicAuthUtils.extractUsername(credentials)
        );
    }

    @Override
    public void signup(CreateUserRequest createUserRequest) {
        authService.signup(createUserRequest);
    }

    @Override
    public ResponseEntity<UserSessionResponse> refreshSession(String refreshToken) {
        return authService.refreshSession(refreshToken);
    }
}
