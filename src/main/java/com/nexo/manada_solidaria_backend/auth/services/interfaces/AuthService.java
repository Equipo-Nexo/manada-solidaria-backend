package com.nexo.manada_solidaria_backend.auth.services.interfaces;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<LoginResponse> login(String username);

    void signup(CreateUserRequest createUserRequest);

    ResponseEntity<LoginResponse> refreshSession(String refreshToken);
}
