package com.nexo.manada_solidaria_backend.auth.utils;

public class WhitelistUtils {
    private WhitelistUtils() {

    }

    public static String[] ENDPOINTS_WHITELIST = {
            "/docs",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/info"
    };

    public static String[] AUTHENTICATION_WHITELIST = {
            "/auth/sign-up",
            "/auth/restore-password",
            "/auth/restore-password/confirm",
            "/auth/restore-password/token/validate",
            "/ping"
    };
}
