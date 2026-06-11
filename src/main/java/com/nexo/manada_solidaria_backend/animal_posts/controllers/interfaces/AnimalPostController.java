package com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/animal-post")
public interface AnimalPostController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AnimalPostResponse create(
            @Valid @RequestBody CreateAnimalPostRequest request,
            @AuthenticationPrincipal User owner
    );
}
