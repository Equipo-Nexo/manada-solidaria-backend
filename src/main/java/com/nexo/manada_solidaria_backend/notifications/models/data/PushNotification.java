package com.nexo.manada_solidaria_backend.notifications.models.data;

/**
 * Not an entity but a record to represent a push notification payload.
 *
 * @param title
 * @param body
 * @param icon
 * @param url
 */
public record PushNotification(
        String title,
        String body,
        String icon,
        String url
) {
}
