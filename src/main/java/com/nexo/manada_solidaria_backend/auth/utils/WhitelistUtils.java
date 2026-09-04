package com.nexo.manada_solidaria_backend.auth.utils;

public class WhitelistUtils {
    private WhitelistUtils() {

    }

    public static String[] ENDPOINTS_WHITELIST = {
            "/docs",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/info",
            "/auth/signup",
            "/password-recovery/**"
    };

    public static String[] BEARER_TOKEN_FILTER_WHITELIST = {
            "/docs",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/info",
            "/auth/signup",
            "/auth/login",
            "/password-recovery"
    };
}
