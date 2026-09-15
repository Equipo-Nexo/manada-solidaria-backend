package com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.models.data.*;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PushNotifier extends NotificationResolver {

    private final ObjectMapper objectMapper;
    protected final PushSuscriptionRepository pushSuscriptionRepository;
    private final PushService pushService;

    public PushNotifier(NotificationDeliveryService notificationDeliveryService, ObjectMapper objectMapper, PushSuscriptionRepository pushSuscriptionRepository, PushService pushService) {
        super(notificationDeliveryService);
        this.objectMapper = objectMapper;
        this.pushSuscriptionRepository = pushSuscriptionRepository;
        this.pushService = pushService;
    }

    @Override
    public void notify(User user, Notification notification) {
        log.info("Sending notification to user {}", user.getId());
        List<PushSubscription> subscriptions = pushSuscriptionRepository.findAllByUser(user);
        subscriptions.forEach(subscription -> {
            NotificationDelivery delivery = createPendingNotificationDelivery(user, notification);
            try {
                send(subscription, new PushNotification(
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getIcon(),
                        notification.getRedirectTo()
                ));
                markNotificationAsSent(delivery);
            } catch (Exception e) {
                log.error(
                        "Failed to send push notification to user {}: subscription={}",
                        user.getId(),
                        subscription.getId(),
                        e
                );
                markNotificationAsFailed(delivery);
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
