package com.nexo.manada_solidaria_backend.users.data.enums;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Rol implements GrantedAuthority {
    VET("VET"),
    RESCUER("RESCUER"),
    TRANSITIONAL_HOME("TRANSITIONAL_HOME"),
    CARRIAGE("CARRIAGE"),
    COMMUNITY("COMMUNITY");

    private final String name;

    Rol(String name) {
        this.name = name;
    }

    @Override
    public @Nullable String getAuthority() {
        return name;
    }
}
