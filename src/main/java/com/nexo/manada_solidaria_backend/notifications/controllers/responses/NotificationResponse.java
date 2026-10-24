package com.nexo.manada_solidaria_backend.notifications.controllers.responses;

import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationPayload;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations.RedirectAction;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record NotificationResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime createdAt,
        boolean readed,
        String redirectTo,
        List<NotificationAction> actions
) {

    public static NotificationResponse from(NotificationDelivery delivery, NotificationPayload payload) {
        return new NotificationResponse(
                delivery.getId(),
                payload.getTitle(),
                payload.getMessage(),
                delivery.getCreatedAt(),
                delivery.isRead(),
                getRedirectToByNotificationActions(delivery),
                getActions(delivery)
        );
    }

    // Metodo utilizado para mantener la compatibilidad con las notificaciones actuales del frontend.
    // Se debe reemplazar una vez que el frontend migre al uso de las "actions".
    private static String getRedirectToByNotificationActions(NotificationDelivery delivery) {
        return getActions(delivery)
                .stream()
                .filter(action -> action instanceof RedirectAction)
                .map(action -> ((RedirectAction) action).getUrl())
                .findFirst()
                .orElse(null);
    }

    private static List<NotificationAction> getActions(NotificationDelivery delivery) {
        return delivery
                .getPayload()
                .getActions();
    }
}
