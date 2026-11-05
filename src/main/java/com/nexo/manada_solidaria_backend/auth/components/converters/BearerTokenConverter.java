package com.nexo.manada_solidaria_backend.auth.components.converters;

import com.nexo.manada_solidaria_backend.auth.data.BearerTokenAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import static com.nexo.manada_solidaria_backend.auth.utils.SecurityConstants.BEARER_PREFIX;

@Component
@Slf4j
public class BearerTokenConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader.contains(BEARER_PREFIX)) {
            return BearerTokenAuthentication.unauthenticated(authorizationHeader.substring(BEARER_PREFIX.length()));
        }

        return null;
    }
}
