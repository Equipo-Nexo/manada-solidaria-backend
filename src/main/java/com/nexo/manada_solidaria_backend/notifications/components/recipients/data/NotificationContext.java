package com.nexo.manada_solidaria_backend.notifications.components.recipients.data;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;

import java.util.UUID;

public record NotificationContext(
        UUID postOwnerId,
        AnimalTraits animal
) {

    public NotificationContext(UUID postOwnerId) {
        this(postOwnerId, null);
    }

    public record AnimalTraits(
            AnimalType type,
            String color
    ) {
    }
}
