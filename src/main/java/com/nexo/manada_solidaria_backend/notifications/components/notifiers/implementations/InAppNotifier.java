package com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InAppNotifier implements NotificationResolver {
    @Override
    public void sendNotification(User user, Notification notification) {
        log.debug("IN APP Notification created successfully.");
    }

    @Override
    public NotificationChannel getNotificationChannel() {
        return NotificationChannel.IN_APP;
    }
}
