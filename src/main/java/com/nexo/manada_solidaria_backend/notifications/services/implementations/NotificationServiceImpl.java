package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientFactory;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDeliveryService notificationDeliveryService;
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

        Set<User> recipients = notificationRecipientFactory
                .resolve(type)
                .getRecipients();

        Set<NotificationResolver> senders = notificationResolvers
                .stream()
                .filter(sender -> type.getChannels().contains(sender.getNotificationChannel()))
                .collect(Collectors.toSet());

        senders.forEach(sender -> {
            recipients.forEach(user -> {
                log.debug("Create pending delivery for user {} with channel {}", user.getId(), sender.getNotificationChannel());
                NotificationDelivery delivery = createPendingNotificationDelivery(sender, user, notification);
                try {
                    log.debug("Sending notification {}", notification.getTitle());
                    sender.sendNotification(user, notification);
                    notificationDeliveryService.markAsSent(delivery);
                } catch (Exception e) {
                    log.error("Error sending notification to user {}: {}", user.getId(), e.getMessage());
                    notificationDeliveryService.markAsFailed(delivery);
                }
            });
        });
    }

    private NotificationDelivery createPendingNotificationDelivery(NotificationResolver sender, User user, Notification notification) {
        return notificationDeliveryService.createNotificationDelivery(user, notification, sender.getNotificationChannel());
    }
}
