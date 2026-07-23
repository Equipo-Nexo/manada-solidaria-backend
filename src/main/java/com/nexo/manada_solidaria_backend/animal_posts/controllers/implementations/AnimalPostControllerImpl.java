package com.nexo.manada_solidaria_backend.animal_posts.controllers.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces.AnimalPostController;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AnimalPostControllerImpl implements AnimalPostController {

    private final AnimalPostService animalPostService;

    @Override
    public AnimalPostResponse create(CreateAnimalPostRequest request, User owner) {
        return animalPostService.create(request, owner);
    }

    @Override
    public Page<AnimalPostResponse> getAnimalPosts(AnimalPostFilter type, String status, Pageable pageable) {
        return animalPostService.getAnimalPosts(type, status, pageable);
    }

    @Override
    public AnimalPostResponse getAnimalPost(UUID animalPostId) {
        return animalPostService.getAnimalPost(animalPostId);
    }

    @Override
    public void update(UUID animalPostId, UpdateAnimalPostRequest request, User authenticatedUser) {
        animalPostService.update(animalPostId, request, authenticatedUser);
    }

    @Override
    public void delete(UUID animalPostId, User authenticatedUser) {
        animalPostService.delete(animalPostId, authenticatedUser);
    }
}
