package com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushNotification;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class PushNotifier implements NotificationResolver {

    private final ObjectMapper objectMapper;
    protected final PushSuscriptionRepository pushSuscriptionRepository;
    private final PushService pushService;

    public PushNotifier(ObjectMapper objectMapper, PushSuscriptionRepository pushSuscriptionRepository, PushService pushService) {
        this.objectMapper = objectMapper;
        this.pushSuscriptionRepository = pushSuscriptionRepository;
        this.pushService = pushService;
    }

    @Override
    public void sendNotification(User user, com.nexo.manada_solidaria_backend.notifications.models.data.Notification notification) {
        List<PushSubscription> subscriptions = pushSuscriptionRepository.findAllByUser(user);
        log.debug("Sending notification to user {}", user.getId());
        subscriptions.forEach(subscription -> {
            try {
                send(subscription, new PushNotification(
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getIcon(),
                        notification.getRedirectTo()
                ));
            } catch (Exception e) {
                log.error(
                        "Failed to send push notification to user {}: subscription={}",
                        user.getId(),
                        subscription.getId(),
                        e
                );
            }
        });
    }

    @Override
    public NotificationChannel getNotificationChannel() {
        return NotificationChannel.PUSH;
    }

    private void send(
            PushSubscription subscription,
            PushNotification payload
    ) throws IOException, JoseException, GeneralSecurityException, ExecutionException, InterruptedException {
        String json = objectMapper.writeValueAsString(payload);
        HttpResponse response = pushService.send(new nl.martijndwars.webpush.Notification(
                subscription.getEndpoint(),
                subscription.getP256dh(),
                subscription.getAuth(),
                json
        ));
        int statusCode = response.getStatusLine().getStatusCode();

        if (!wasSent(statusCode)) {
            log.error(
                    "Push notification failed. Status: {}, Reason: {}",
                    statusCode,
                    response.getStatusLine().getReasonPhrase()
            );
            throw new IOException("Failed to send push notification. Status: " + statusCode);
        }
    }

    private static boolean wasSent(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }


}
