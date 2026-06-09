package com.nexo.manada_solidaria_backend.auth.components;

import com.nexo.manada_solidaria_backend.auth.data.BearerTokenAuthentication;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BearerAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = (User) userService.loadUserByUsername(getUsername(authentication));
        return BearerTokenAuthentication.authenticated(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }

    private String getUsername(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

}
