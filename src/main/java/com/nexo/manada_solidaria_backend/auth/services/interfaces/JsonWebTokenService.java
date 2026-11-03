package com.nexo.manada_solidaria_backend.auth.services.interfaces;

import com.nexo.manada_solidaria_backend.auth.services.implementations.JsonWebTokenServiceImpl;

import java.util.Map;

public interface JsonWebTokenService {
    String getRefreshToken(String subject, Map<String, Object> claims);

    String getAccessToken(String subject, Map<String, Object> claims);

    String getSubject(String token, JsonWebTokenServiceImpl.TokenType type);

    boolean isValid(String token, JsonWebTokenServiceImpl.TokenType type);
}
