package com.nexo.manada_solidaria_backend.notifications.services.interfaces;

import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface PushNotificationService {
    void suscribe(User user, PushNotificationSubscriptionRequest request);

    void unsubscribe(User user, PushNotificationUnsuscribeRequest request);
}
