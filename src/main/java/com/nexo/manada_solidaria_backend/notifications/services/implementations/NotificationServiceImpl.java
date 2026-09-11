package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientFactory;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientFactory notificationRecipientFactory;
    private final List<NotificationResolver> notificationResolvers;


    @Override
    @Async("notificationExecutor")
    public void notify(NotificationType type) {
        log.info("Sending notification {}", type);
        Notification notification = notificationRepository
                .findByType(type)
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Notification not found for type: " + type));

        notificationRecipientFactory
                .resolve(type)
                .getRecipients()
                .forEach(user -> {
                    try {
                        log.debug("Sending notification {} to user {}", notification.getTitle(), user.getId());
                        notificationResolvers
                                .stream()
                                .filter(sender -> type.getChannels().contains(sender.getNotificationChannel()))
                                .forEach(sender -> {
                                    log.debug("Sending notification with sender {}", sender);
                                    sender.sendNotification(user, notification);
                                });
                    } catch (Exception e) {
                        log.error("Error sending notification to user {}: {}", user.getId(), e.getMessage());
                    }
                });
    }
}
