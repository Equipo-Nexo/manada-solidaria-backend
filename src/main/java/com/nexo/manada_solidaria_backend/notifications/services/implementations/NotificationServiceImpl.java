package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.NotificationRecipientFactory;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationPayload;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    /**
     * Sends a notification to a set of recipients based on the provided notification type, parameters, and context.
     */
    public void notify(NotificationType type, Map<String, Object> params, User recipient) {
        sendNotificationsToUsers(params, Set.of(recipient), type);
    }

    @Override
    @Async("notificationExecutor")
    @Transactional
    /**
     * Sends a notification to recipients determined by the notification type and context, using the provided parameters.
     */
    public void notify(NotificationType type, Map<String, Object> params, NotificationContext context) {
        log.info("Sending notification {}", type);
        Set<User> recipients = getRecipientsByNotificationType(type, context);
        sendNotificationsToUsers(params, recipients, type);
    }

    private void sendNotificationsToUsers(Map<String, Object> params, Set<User> recipients, NotificationType type) {
        NotificationPayload payload = this.buildNotificationPayload(type, params);
        Set<NotificationResolver> senders = notificationResolvers
                .stream()
                .filter(sender -> type.getChannels().contains(sender.getNotificationChannel()))
                .collect(Collectors.toSet());

        senders.forEach(sender ->
                recipients.forEach(user ->
                                log.info("Sending notification payload is [{}]", payload)
                        // sender.notify(user, payload, type)
                )
        );
    }

    private Set<User> getRecipientsByNotificationType(NotificationType type, NotificationContext context) {
        return notificationRecipientFactory
                .resolve(type)
                .getRecipients(context);
    }

    private NotificationPayload buildNotificationPayload(NotificationType type, Map<String, Object> params) {
        Notification notification = notificationRepository
                .findByType(type)
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Notification not found for type: " + type));

        return replaceParams(
                new NotificationPayload(
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getIcon(),
                        notification.getActions()
                ),
                params
        );
    }

    private NotificationPayload replaceParams(NotificationPayload notification, Map<String, Object> params) {
        notification.getActions().forEach(action -> action.replaceParams(params));
        params.forEach((key, value) -> {
            String placeholder = "{" + key + "}";

            notification.setTitle(
                    notification.getTitle().replace(placeholder, value.toString())
            );

            notification.setMessage(
                    notification.getMessage().replace(placeholder, value.toString())
            );
        });

        return notification;
    }
}
