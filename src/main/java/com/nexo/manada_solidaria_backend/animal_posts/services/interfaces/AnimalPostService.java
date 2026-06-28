package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimalPostService {
    AnimalPostResponse create(CreateAnimalPostRequest request, User owner);

    Page<AnimalPostResponse> getAnimalPosts(AnimalPostType type, String status, Pageable pageable);
}
