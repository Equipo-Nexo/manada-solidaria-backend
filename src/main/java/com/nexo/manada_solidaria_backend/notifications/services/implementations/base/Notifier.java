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
import org.springframework.scheduling.annotation.Async;

/**
 * Abstract implementation of the NotificationService interface.
 * This class provides a base implementation for notifying users with notifications.
 */
@Slf4j
public abstract class Notifier implements NotificationService {

    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    protected Notifier(
            NotificationRepository notificationRepository,
            NotificationDeliveryRepository notificationDeliveryRepository,
            UserService userService
    ) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.notificationDeliveryRepository = notificationDeliveryRepository;
    }

    @Override
    @Async("notificationExecutor")
    public void notify(Notification notification) {
        log.info("Sending notification {}", notification);
        notificationRepository.save(notification);
        userService
                .findAll()
                .forEach(user -> {
                    try {
                        log.debug("Sending notification {} to user {}", notification.getTitle(), user.getId());
                        sendNotification(user, notification);
                    } catch (Exception e) {
                        log.error("Error sending notification to user {}: {}", user.getId(), e.getMessage());
                        recordNotificationDeliveryFailed(user, notification);
                    }
                });
    }

    protected void recordNotificationDeliverySuccess(
            User user,
            Notification notification
    ) {
        recordNotificationDelivery(user, notification, NotificationStatus.SENT);
    }

    protected void recordNotificationDeliveryFailed(
            User user,
            Notification notification
    ) {
        recordNotificationDelivery(user, notification, NotificationStatus.FAILED);
    }

    private void recordNotificationDelivery(User user, Notification notification, NotificationStatus status) {
        notificationDeliveryRepository.save(new NotificationDelivery(
                user,
                notification,
                getNotificationChannel(),
                status
        ));
    }

    public abstract void sendNotification(User user, Notification notification);

    public abstract NotificationChannel getNotificationChannel();
}
