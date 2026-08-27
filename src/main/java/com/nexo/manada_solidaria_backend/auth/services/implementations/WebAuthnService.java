package com.nexo.manada_solidaria_backend.auth.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.LoginResponse;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class WebAuthnService implements AuthenticationSuccessHandler {
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        String username = authentication.getName();
        LoginResponse loginResponse = authService.login(username);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                loginResponse
        );
    }
}
