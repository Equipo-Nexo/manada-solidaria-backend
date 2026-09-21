package com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations;

import com.nexo.manada_solidaria_backend.notifications.clients.WebPushClient;
import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.models.data.*;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.List;

@Slf4j
public class PushNotifier extends NotificationResolver {

    protected final PushSuscriptionRepository pushSuscriptionRepository;
    private final WebPushClient pushClient;

    public PushNotifier(NotificationDeliveryService notificationDeliveryService, PushSuscriptionRepository pushSuscriptionRepository, WebPushClient pushClient) {
        super(notificationDeliveryService);
        this.pushSuscriptionRepository = pushSuscriptionRepository;
        this.pushClient = pushClient;
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
    ) throws Exception {
        HttpResponse response = pushClient.send(subscription, payload);
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
