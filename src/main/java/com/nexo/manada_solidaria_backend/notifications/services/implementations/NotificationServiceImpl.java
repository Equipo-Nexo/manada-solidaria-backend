package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientFactory;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientFactory notificationRecipientFactory;


    @Override
    @Async("notificationExecutor")
    public void notify(NotificationType type) {
        log.info("Sending notification {}", type);
        notificationRepository.save(type);

        notificationRecipientFactory
                .resolve(type)
                .getRecipients()
                .forEach(user -> {
                    try {
                        log.debug("Sending notification {} to user {}", notification.getTitle(), user.getId());
                        // sendNotification(user, notification);
                    } catch (Exception e) {
                        log.error("Error sending notification to user {}: {}", user.getId(), e.getMessage());
                        // recordNotificationDeliveryFailed(user, notification);
                    }
                });
    }


   // protected void recordNotificationDeliverySuccess(
   //         User user,
   //         Notification notification
   // ) {
   //     recordNotificationDelivery(user, notification, NotificationStatus.SENT);
   // }
//
   // protected void recordNotificationDeliveryFailed(
   //         User user,
   //         Notification notification
   // ) {
   //     recordNotificationDelivery(user, notification, NotificationStatus.FAILED);
   // }
//
   // private void recordNotificationDelivery(User user, Notification notification, NotificationStatus status) {
   //     notificationDeliveryRepository.save(new NotificationDelivery(
   //             user,
   //             notification,
   //             getNotificationChannel(),
   //             status
   //     ));
   // }

}
