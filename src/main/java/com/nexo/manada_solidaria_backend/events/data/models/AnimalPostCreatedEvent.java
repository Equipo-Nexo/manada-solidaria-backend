package com.nexo.manada_solidaria_backend.events.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;

import java.util.UUID;

public record AnimalPostCreatedEvent(
        UUID postId,
        UUID ownerId,
        boolean needTransport,
        AnimalPostFilter type,
        Location location,
        String status,
        NotificationContext context
) implements BaseAnimalPostStatusChanged {
}
