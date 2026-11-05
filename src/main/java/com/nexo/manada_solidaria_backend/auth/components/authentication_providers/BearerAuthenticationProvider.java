package com.nexo.manada_solidaria_backend.auth.components.authentication_providers;

import com.nexo.manada_solidaria_backend.auth.data.BearerTokenAuthentication;
import com.nexo.manada_solidaria_backend.auth.services.implementations.JsonWebTokenServiceImpl;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class BearerAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final JsonWebTokenService jsonWebTokenService;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = getAuthenticatedUser(authentication);
        return BearerTokenAuthentication.authenticated(
                user,
                authentication.getCredentials(),
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.isAssignableFrom(authentication);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userService.getUserById(getUserId(authentication));
    }

    private UUID getUserId(Authentication authentication) {
        try {
            String token = authentication.getCredentials().toString();
            return Optional.ofNullable(getSubject(token))
                .map(Object::toString)
                .map(UUID::fromString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token is invalid"));
        } catch (Exception e) {
            log.error("Error getting user from JWT");
            return null;
        }
    }

    private String getSubject(String token) {
        return jsonWebTokenService.getSubject(token, JsonWebTokenServiceImpl.TokenType.ACCESS);
    }

}
