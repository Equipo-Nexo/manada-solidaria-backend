package com.nexo.manada_solidaria_backend.auth.controllers.responses;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
