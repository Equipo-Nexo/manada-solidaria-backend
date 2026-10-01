package com.nexo.manada_solidaria_backend.notifications.components.recipients.data;

import java.util.UUID;

public record NotificationContext(
        UUID postOwnerId
) {
}
