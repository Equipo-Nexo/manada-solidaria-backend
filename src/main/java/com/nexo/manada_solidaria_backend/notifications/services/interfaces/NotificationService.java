package com.nexo.manada_solidaria_backend.notifications.services.interfaces;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;

public interface NotificationService {
    void notify(NotificationType type);
}
