package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AnimalPostService {
    AnimalPostResponse create(CreateAnimalPostRequest request, User owner);

    Page<AnimalPostResponse> getAnimalPosts(AnimalPostFilter type, String status, Pageable pageable);

    void update(UUID animalPostId, UpdateAnimalPostRequest request, User authenticatedUser);

    void delete(UUID animalPostId, User authenticatedUser);

    List<AnimalPostResponse> getUserAnimalPosts(User user);
}
