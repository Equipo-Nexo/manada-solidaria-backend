package com.nexo.manada_solidaria_backend.notifications.services.implementations.base;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.base.NotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Abstract implementation of the NotificationService interface.
 * This class provides a base implementation for notifying users with notifications.
 */
@Slf4j
public abstract class NotificationServiceImpl implements NotificationService {

    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    protected NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationDeliveryRepository notificationDeliveryRepository,
            UserService userService
    ) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.notificationDeliveryRepository = notificationDeliveryRepository;
    }

    @Override
    public void notify(Notification notification) {
        log.info("Sending notification {}", notification);
        notificationRepository.save(notification);

        List<User> usersToNotify = userService.findAll();
        usersToNotify
                .forEach(user -> {
                    try {
                        log.debug("Sending notification {} to user {}", notification.getTitle(), user.getId());
                        sendNotification(user, notification);
                        recordNotificationDelivery(user, notification, NotificationStatus.SENT);
                    } catch (Exception e) {
                        log.error("Error sending notification to user {}: {}", user.getId(), e.getMessage());
                        recordNotificationDelivery(user, notification, NotificationStatus.FAILED);
                    }
                });
    }

    private void recordNotificationDelivery(
            User user,
            Notification notification,
            NotificationStatus notificationStatus
    ) {
        notificationDeliveryRepository.save(new NotificationDelivery(
                user,
                notification,
                getNotificationChannel(),
                notificationStatus
        ));
    }

    public abstract void sendNotification(User user, Notification notification);

    public abstract NotificationChannel getNotificationChannel();
}
