package com.nexo.manada_solidaria_backend.notifications.controllers.responses;

import java.util.List;

public record UserNotificationsResponse(
        boolean hasUnreadNotification,
        List<NotificationResponse> notifications
) {
}
