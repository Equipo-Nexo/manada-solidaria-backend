package com.nexo.manada_solidaria_backend.images.controllers.responses;

import java.time.Instant;

public record PresignedUrlResponse(
        String imageId,
        String uploadUrl,
        Instant expiresAt
) {
}
