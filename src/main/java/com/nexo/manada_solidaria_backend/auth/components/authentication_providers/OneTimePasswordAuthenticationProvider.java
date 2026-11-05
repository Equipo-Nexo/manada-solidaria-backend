package com.nexo.manada_solidaria_backend.auth.components.authentication_providers;

import com.nexo.manada_solidaria_backend.auth.data.BearerTokenAuthentication;
import com.nexo.manada_solidaria_backend.auth.data.OneTimePasswordAuthentication;
import com.nexo.manada_solidaria_backend.password_recovery.services.interfaces.PasswordRecoveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@AllArgsConstructor
public class OneTimePasswordAuthenticationProvider implements AuthenticationProvider {

    private final PasswordRecoveryService passwordRecoveryService;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            User user = passwordRecoveryService.getUserByTokenOrThrowException(authentication.getCredentials().toString());
            return BearerTokenAuthentication.authenticated(
                    user,
                    authentication.getCredentials(),
                    user.getAuthorities()
            );
        } catch (ResponseStatusException e) {
            log.debug("TOTP was not found");
            return null;
        } catch (Exception e) {
            log.error("Error authentication with OTP {}", e);
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OneTimePasswordAuthentication.class.isAssignableFrom(authentication);
    }
}
