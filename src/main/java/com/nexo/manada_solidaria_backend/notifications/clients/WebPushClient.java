package com.nexo.manada_solidaria_backend.notifications.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushNotification;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WebPushClient {

    private final PushService pushService;
    private final ObjectMapper objectMapper;

    public HttpResponse send(
            PushSubscription subscription,
            PushNotification payload
    ) throws Exception {
        String json = objectMapper.writeValueAsString(payload);

        var notification = new nl.martijndwars.webpush.Notification(
                subscription.getEndpoint(),
                subscription.getP256dh(),
                subscription.getAuth(),
                json
        );

        return pushService.send(notification);
    }
}
