package com.nexo.manada_solidaria_backend.events.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;

import java.util.UUID;

public interface BaseAnimalPostStatusChanged {
    UUID postId();
    UUID ownerId();
    AnimalPostFilter type();
    String status();
}
