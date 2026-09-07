package com.nexo.manada_solidaria_backend.notifications.components.recipients;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationRecipientResolver {

    private final NotificationRecipient getAllUsers;
    private final Map<NotificationType, NotificationRecipient> recipientMap;

    public NotificationRecipientResolver(
            NotificationRecipient getAllUsers,
            NotificationRecipient getCarriers
    ) {
        this.getAllUsers = getAllUsers;
        this.recipientMap = Map.of(
                NotificationType.NEW_CARRIAGE_REQUEST, getCarriers
        );
    }

    public NotificationRecipient resolve(NotificationType notificationType) {
        return recipientMap.getOrDefault(notificationType, getAllUsers);
    }
}
