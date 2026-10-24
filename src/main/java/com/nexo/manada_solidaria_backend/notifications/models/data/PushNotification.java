package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;

import java.util.List;

/**
 * Not an entity but a record to represent a push notification payload.
 *
 * @param title
 * @param body
 * @param icon
 * @param actions
 */
public record PushNotification(
        String title,
        String body,
        String icon,
        List<NotificationAction> actions
) {
}
