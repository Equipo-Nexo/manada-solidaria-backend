package com.nexo.manada_solidaria_backend.notifications.components.notifiers;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NotificationResolver {

    protected final NotificationDeliveryService notificationDeliveryService;

    public NotificationResolver(NotificationDeliveryService notificationDeliveryService) {
        this.notificationDeliveryService = notificationDeliveryService;
    }

    public abstract void notify(User user, Notification notification);

    public abstract NotificationChannel getNotificationChannel();

    protected NotificationDelivery createPendingNotificationDelivery(User user, Notification notification) {
        return notificationDeliveryService.createNotificationDelivery(user, notification, getNotificationChannel());
    }

    protected void markNotificationAsSent(NotificationDelivery delivery) {
        notificationDeliveryService.markAsSent(delivery);
    }

    protected void markNotificationAsFailed(NotificationDelivery delivery) {
        notificationDeliveryService.markAsFailed(delivery);
    }
}
