package com.nexo.manada_solidaria_backend.auth.controllers.responses;

import java.util.UUID;

public record UserSessionResponse(
        UUID userId,
        String accessToken
) {
}
