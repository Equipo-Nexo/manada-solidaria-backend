package com.nexo.manada_solidaria_backend.notifications.services.interfaces;

import com.nexo.manada_solidaria_backend.notifications.controllers.responses.UserNotificationsResponse;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.UUID;

public interface NotificationDeliveryService {
    NotificationDelivery createNotificationDelivery(User user, Notification notification, NotificationChannel channel);

    NotificationDelivery markAsFailed(NotificationDelivery notificationDelivery);

    NotificationDelivery markAsSent(NotificationDelivery notificationDelivery);

    UserNotificationsResponse getUserNotifications(UUID userId, User authenticatedUser);

    void markAsRead(UUID userId, UUID notificationId, User authenticatedUser);

    void markAllAsRead(UUID userId, User authenticatedUser);
}
