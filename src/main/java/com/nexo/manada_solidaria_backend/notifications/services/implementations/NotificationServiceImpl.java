package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientFactory;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientFactory notificationRecipientFactory;
    private final List<NotificationResolver> notificationResolvers;


    @Override
    @Async("notificationExecutor")
    public void notify(NotificationType type, Map<String, Object> params) {
        log.info("Sending notification {}", type);
        Notification notification = notificationRepository
                .findByType(type)
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Notification not found for type: " + type));

        this.replaceParams(notification, params);

        Set<User> recipients = notificationRecipientFactory
                .resolve(type)
                .getRecipients();

        Set<NotificationResolver> senders = notificationResolvers
                .stream()
                .filter(sender -> type.getChannels().contains(sender.getNotificationChannel()))
                .collect(Collectors.toSet());

        senders.forEach(sender ->
                recipients.forEach(user ->
                        sender.notify(user, notification)
                )
        );
    }

    private void replaceParams(Notification notification, Map<String, Object> params) {
        params.forEach((key, value) -> {
            String placeholder = "{" + key + "}";

            notification.setTitle(
                    notification.getTitle().replace(placeholder, value.toString())
            );

            notification.setMessage(
                    notification.getMessage().replace(placeholder, value.toString())
            );

            notification.setRedirectTo(
                    notification.getRedirectTo().replace(placeholder, value.toString())
            );
        });
    }
}
