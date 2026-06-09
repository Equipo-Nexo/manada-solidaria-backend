package com.nexo.manada_solidaria_backend.auth.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.common.exceptions.responses.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Primary
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public CustomBasicAuthenticationEntryPoint(ObjectMapper mapper) {
        super.setRealmName("Manada-Solidaria");
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(mapper.writeValueAsString(
                buildApiError(request, authException)
        ));
    }

    private static ApiError buildApiError(HttpServletRequest request, AuthenticationException authException) {
        String message = getErrorMessage(authException);
        return new ApiError(
                SC_UNAUTHORIZED,
                List.of(message),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    private static String getErrorMessage(AuthenticationException authException) {
        String errorMessage = "Hubo un error al autenticar. Intente nuevamente";
        if (authException instanceof BadCredentialsException || authException instanceof InternalAuthenticationServiceException) {
            errorMessage = "Las credenciales ingresadas son incorrectas";
        }
        return errorMessage;
    }

}