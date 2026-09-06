package com.nexo.manada_solidaria_backend.notifications.controllers.requests;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for unsubscribing from push notifications.
 *
 * @param endpoint The endpoint URL for the push notification subscription to be unsubscribed.
 */
public record PushNotificationUnsuscribeRequest(
        @NotBlank
        String endpoint
) {
}
