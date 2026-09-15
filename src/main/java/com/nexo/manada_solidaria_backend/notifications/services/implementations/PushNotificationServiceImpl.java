package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations.PushNotifier;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.PushNotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class PushNotificationServiceImpl extends PushNotifier implements PushNotificationService {

    public PushNotificationServiceImpl(ObjectMapper objectMapper, PushSuscriptionRepository pushSuscriptionRepository, PushService pushService) {
        super(objectMapper, pushSuscriptionRepository, pushService);
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
