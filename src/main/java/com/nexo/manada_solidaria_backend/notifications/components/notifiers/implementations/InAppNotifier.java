package com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InAppNotifier extends NotificationResolver {

    public InAppNotifier(NotificationDeliveryService notificationDeliveryService) {
        super(notificationDeliveryService);
    }

    @Override
    public void notify(User user, Notification notification) {
        NotificationDelivery delivery = createPendingNotificationDelivery(user, notification);
        markNotificationAsSent(delivery);
    }

    @Override
    public NotificationChannel getNotificationChannel() {
        return NotificationChannel.IN_APP;
    }
}
