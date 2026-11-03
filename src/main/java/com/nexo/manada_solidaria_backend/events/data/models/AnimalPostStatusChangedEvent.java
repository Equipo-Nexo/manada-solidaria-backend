package com.nexo.manada_solidaria_backend.events.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;

import java.util.UUID;


/**
 * Event published when an AnimalPost changes its status.
 *
 * @param postId    identifier of the animal post
 * @param ownerId   identifier of the post owner
 * @param oldStatus previous status of the post
 * @param status new status of the post
 */
public record AnimalPostStatusChangedEvent(
        UUID postId,
        UUID ownerId,
        String oldStatus,
        Enum<?> status,
        AnimalPostFilter type
) implements BaseAnimalPostStatusChanged {
}
