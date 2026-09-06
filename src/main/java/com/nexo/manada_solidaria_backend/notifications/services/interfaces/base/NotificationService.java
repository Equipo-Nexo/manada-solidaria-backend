package com.nexo.manada_solidaria_backend.notifications.services.interfaces.base;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;

public interface NotificationService {
    void notify(Notification notification);
}
