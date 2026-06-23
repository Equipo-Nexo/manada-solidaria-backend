package com.nexo.manada_solidaria_backend.animal_posts.controllers.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces.AnimalPostController;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AnimalPostControllerImpl implements AnimalPostController {

    private final AnimalPostService animalPostService;

    @Override
    public AnimalPostResponse create(CreateAnimalPostRequest request, User owner) {
        return animalPostService.create(request, owner);
    }
}
