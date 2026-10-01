package com.nexo.manada_solidaria_backend.notifications.controllers.responses;

import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime createdAt,
        boolean readed,
        String redirectTo
) {

    public static NotificationResponse from(NotificationDelivery delivery) {
        return new NotificationResponse(
                delivery.getId(),
                delivery.getTitle(),
                delivery.getMessage(),
                delivery.getCreatedAt(),
                delivery.isRead(),
                delivery.getRedirectTo()
        );
    }
}
