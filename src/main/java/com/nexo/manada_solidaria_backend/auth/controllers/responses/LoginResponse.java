package com.nexo.manada_solidaria_backend.auth.controllers.responses;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String accessToken,
        String refreshToken
) {
}
