package com.nexo.manada_solidaria_backend.animal_post.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_post.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimalPostService {
    AnimalPostResponse create(CreateAnimalPostRequest request, User owner);

    Page<AnimalPostResponse> findAll(AnimalPostFilter type, Pageable pageable);
}
