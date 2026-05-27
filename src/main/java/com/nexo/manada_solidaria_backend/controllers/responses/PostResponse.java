package com.nexo.manada_solidaria_backend.controllers.responses;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String description,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
