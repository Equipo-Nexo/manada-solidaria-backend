package com.nexo.manada_solidaria_backend.notifications.controllers.responses;

import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;

import java.util.List;

public record UserNotificationsResponse(
        boolean hasUnreadNotification,
        List<NotificationResponse> notifications
) {

    public static UserNotificationsResponse from(List<NotificationDelivery> deliveries) {
        return new UserNotificationsResponse(
                deliveries.stream().anyMatch(delivery -> !delivery.isRead()),
                deliveries.stream().map(NotificationResponse::from).toList()
        );
    }
}
