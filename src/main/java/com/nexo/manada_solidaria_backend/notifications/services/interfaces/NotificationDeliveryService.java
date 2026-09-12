package com.nexo.manada_solidaria_backend.notifications.services.interfaces;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface NotificationDeliveryService {
    NotificationDelivery createNotificationDelivery(User user, Notification notification, NotificationChannel channel);

    NotificationDelivery markAsFailed(NotificationDelivery notificationDelivery);

    NotificationDelivery markAsSent(NotificationDelivery notificationDelivery);
}
