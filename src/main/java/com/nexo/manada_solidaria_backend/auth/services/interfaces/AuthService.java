package com.nexo.manada_solidaria_backend.auth.services.interfaces;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.LoginResponse;

public interface AuthService {
    LoginResponse login(String username);

    void signup(CreateUserRequest createUserRequest);
}
