package com.nexo.manada_solidaria_backend.notifications.controllers.implementations;

import com.nexo.manada_solidaria_backend.notifications.controllers.interfaces.PushNotificationController;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.PushNotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PushNotificationControllerImpl implements PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @Override
    public void suscribe(User user, PushNotificationSubscriptionRequest request) {
        pushNotificationService.suscribe(user, request);
    }

    @Override
    public void unsubscribe(User user, PushNotificationUnsuscribeRequest request) {
        pushNotificationService.unsubscribe(user, request);
    }
}
