package com.nexo.manada_solidaria_backend.animal_post.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_post.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface AnimalPostService {
    AnimalPostResponse create(CreateAnimalPostRequest request, User owner);
}
