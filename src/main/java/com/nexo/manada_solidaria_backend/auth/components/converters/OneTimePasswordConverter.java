package com.nexo.manada_solidaria_backend.auth.components.converters;

import com.nexo.manada_solidaria_backend.auth.data.OneTimePasswordAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import static com.nexo.manada_solidaria_backend.auth.utils.SecurityConstants.OTP_PREFIX;

@Component
public class OneTimePasswordConverter implements AuthenticationConverter {
    @Override
    public @Nullable Authentication convert(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader.contains(OTP_PREFIX)) {
            return OneTimePasswordAuthentication.unauthenticated(authorizationHeader.substring(OTP_PREFIX.length()));
        }

        return null;
    }
}
