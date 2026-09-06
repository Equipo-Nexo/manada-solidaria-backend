package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushNotification;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.PushNotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

    private final PushService pushService;
    private final PushSuscriptionRepository pushSuscriptionRepository;
    private final ObjectMapper objectMapper;

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

    @Override
    public void notify(User user, PushNotification pushNotification) {
        List<PushSubscription> subscriptions = pushSuscriptionRepository.findAllByUser(user);
        log.debug("Sending notification to user {}", user.getId());
        subscriptions.forEach(subscription ->
                send(subscription, pushNotification)
        );
    }

    private void send(
            PushSubscription subscription,
            PushNotification payload
    ) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            Notification notification = new Notification(
                    subscription.getEndpoint(),
                    subscription.getP256dh(),
                    subscription.getAuth(),
                    json
            );
            HttpResponse response = pushService.send(notification);

            log.debug("Push notification sent: subscription={}", subscription.getId());
            log.info(
                    "Push sent: status={} reason={}",
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase()
            );
        } catch (Exception exception) {
            log.error(
                    "Failed to send push notification: subscription={}",
                    subscription.getId(),
                    exception
            );
        }
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
