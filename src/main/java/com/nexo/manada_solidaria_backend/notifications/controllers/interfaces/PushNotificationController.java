package com.nexo.manada_solidaria_backend.notifications.controllers.interfaces;

import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/push-notifications")
public interface PushNotificationController {

    @PostMapping("/subscribe")
    @ResponseStatus(NO_CONTENT)
    void suscribe(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PushNotificationSubscriptionRequest request
    );

    @DeleteMapping("/unsubscribe")
    @ResponseStatus(NO_CONTENT)
    void unsubscribe(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PushNotificationUnsuscribeRequest request
    );

    @PostMapping("/test")
    @ResponseStatus(NO_CONTENT)
    void test(
            @AuthenticationPrincipal User user
    );
}
