package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushNotification;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.implementations.base.Notifier;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.PushNotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class PushNotificationServiceImpl extends Notifier implements PushNotificationService {

    private final PushService pushService;
    private final PushSuscriptionRepository pushSuscriptionRepository;
    private final ObjectMapper objectMapper;

    public PushNotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationDeliveryRepository notificationDeliveryRepository,
            PushService pushService,
            PushSuscriptionRepository pushSuscriptionRepository,
            ObjectMapper objectMapper,
            UserService userService
    ) {
        super(notificationRepository, notificationDeliveryRepository, userService);
        this.pushService = pushService;
        this.pushSuscriptionRepository = pushSuscriptionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void suscribe(User user, PushNotificationSubscriptionRequest request) {
        log.debug("Subscribing user {} to push notifications", user.getId());
        PushSubscription subscription = pushSuscriptionRepository
                .findByEndpoint(request.endpoint())
                .orElseGet(this::createEmptySubscription);

        subscription.setUser(user);
        subscription.setEndpoint(request.endpoint());
        subscription.setEndpointHash(hashEndpoint(subscription.getEndpoint()));
        subscription.setP256dh(request.key().p256dh());
        subscription.setAuth(request.key().auth());

        pushSuscriptionRepository.save(subscription);
        log.debug("User {} subscribed to push notifications: subscription={}", user.getId(), subscription.getId());
    }

    @Override
    @Transactional
    public void unsubscribe(User user, PushNotificationUnsuscribeRequest request) {
        log.debug("Unsubscribing user {} from push notifications", user.getId());
        pushSuscriptionRepository.deleteByEndpointAndUser(
                request.endpoint(),
                user
        );
    }

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
                recordNotificationDeliverySuccess(user, notification);
            } catch (Exception e) {
                log.error(
                        "Failed to send push notification to user {}: subscription={}",
                        user.getId(),
                        subscription.getId(),
                        e
                );
                recordNotificationDeliveryFailed(user, notification);
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
        HttpResponse response = pushService.send(new Notification(
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

    private PushSubscription createEmptySubscription() {
        return new PushSubscription();
    }

    private byte[] hashEndpoint(String endpoint) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(endpoint.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
