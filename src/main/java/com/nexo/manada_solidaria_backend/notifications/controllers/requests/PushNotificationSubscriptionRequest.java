package com.nexo.manada_solidaria_backend.notifications.controllers.requests;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for subscribing to push notifications.
 *
 * @param endpoint       The endpoint URL for the push notification subscription.
 * @param expirationTime The expiration time of the subscription (can be null).
 * @param key            The keys associated with the subscription, including p256dh and auth.
 */
public record PushNotificationSubscriptionRequest(
        @NotBlank
        String endpoint,
        String expirationTime,
        @Valid Key key
) {
    public record Key(
            @NotBlank
            String p256dh,
            @NotBlank
            String auth
    ) {
    }
}
