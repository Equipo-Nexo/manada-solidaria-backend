package com.nexo.manada_solidaria_backend.auth.components;

import com.nexo.manada_solidaria_backend.auth.data.BearerTokenAuthentication;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.common.exceptions.custom_exceptions.InvalidJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class BearerTokenConverter implements AuthenticationConverter {
    private final JsonWebTokenService jsonWebTokenService;
    private final String BEARER_PREFIX = "Bearer ";

    public BearerTokenConverter(JsonWebTokenService jsonWebTokenService) {
        this.jsonWebTokenService = jsonWebTokenService;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeaderIsInvalid(authorizationHeader)) {
            throw new InvalidJwtException("Session is not valid");
        }

        return BearerTokenAuthentication.unauthenticated(getSubject(getJwtValue(authorizationHeader)), authorizationHeader);
    }

    private String getSubject(String jwt) {
        return jsonWebTokenService.getSubject(jwt);
    }

    private boolean authorizationHeaderIsInvalid(String authorizationHeader) {
        return authorizationHeaderIsBlankOrNull(authorizationHeader) ||
                authorizationHeaderDoesNotContainsBearerPrefix(authorizationHeader) ||
                jwtIsInvalid(authorizationHeader);
    }

    private static boolean authorizationHeaderIsBlankOrNull(String authorizationHeader) {
        return authorizationHeader == null || authorizationHeader.isBlank();
    }

    private boolean authorizationHeaderDoesNotContainsBearerPrefix(String authorizationHeader) {
        return !StringUtils.startsWithIgnoreCase(authorizationHeader, BEARER_PREFIX);
    }

    private boolean jwtIsInvalid(String authorizationHeader) {
        try {
            return !jsonWebTokenService.isValid(getJwtValue(authorizationHeader));
        } catch (Exception e) {
            log.error("Error checking if JWT is valid", e);
            return true;
        }
    }

    private String getJwtValue(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
