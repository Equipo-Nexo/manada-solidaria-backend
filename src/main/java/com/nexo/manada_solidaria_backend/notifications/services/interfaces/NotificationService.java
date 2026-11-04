package com.nexo.manada_solidaria_backend.notifications.services.interfaces;

import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.Map;

public interface NotificationService {
    void notify(NotificationType type, Map<String, Object> params, User recipient);

    void notify(NotificationType type, Map<String, Object> params, NotificationContext context);
}
