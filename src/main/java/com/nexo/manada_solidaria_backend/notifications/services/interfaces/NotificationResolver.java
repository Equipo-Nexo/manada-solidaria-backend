package com.nexo.manada_solidaria_backend.notifications.services.interfaces;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface NotificationResolver {
    void sendNotification(User user, Notification notification);
    NotificationChannel getNotificationChannel();
}
