package com.nexo.manada_solidaria_backend.notifications.controllers.implementations;

import com.nexo.manada_solidaria_backend.notifications.controllers.interfaces.NotificationController;
import com.nexo.manada_solidaria_backend.notifications.controllers.responses.UserNotificationsResponse;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationDeliveryService notificationDeliveryService;

    @Override
    public UserNotificationsResponse getNotifications(UUID userId, User authenticatedUser) {
        return notificationDeliveryService.getUserNotifications(userId, authenticatedUser);
    }

    @Override
    public void markAllAsRead(UUID userId, User authenticatedUser) {
        notificationDeliveryService.markAllAsRead(userId, authenticatedUser);
    }

    @Override
    public void markAsRead(UUID userId, UUID notificationId, User authenticatedUser) {
        notificationDeliveryService.markAsRead(userId, notificationId, authenticatedUser);
    }
}
