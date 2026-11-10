package com.nexo.manada_solidaria_backend.auth.data;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class OneTimePasswordAuthentication extends AbstractAuthenticationToken {
    private Object credentials;
    private Object principal;

    public OneTimePasswordAuthentication(
            Object credentials
    ) {
        super(Collections.emptyList());
        this.credentials = credentials;
        this.principal = null;
    }

    public OneTimePasswordAuthentication(
            @Nullable Collection<? extends GrantedAuthority> authorities,
            Object credentials,
            Object principal
    ) {
        super(authorities);
        this.credentials = credentials;
        this.principal = principal;
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.credentials;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return null;
    }

    public static OneTimePasswordAuthentication unauthenticated(Object credentials) {
        return new OneTimePasswordAuthentication(credentials);
    }

    public static OneTimePasswordAuthentication authenticated(Collection<? extends GrantedAuthority> authorities, Object credentials, Object principal) {
        return new OneTimePasswordAuthentication(authorities, credentials, principal);
    }
}
