package com.nexo.manada_solidaria_backend.animal_post.controllers.interfaces;

import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_post.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/animal-post")
public interface AnimalPostController {

    @PostMapping
    ResponseEntity<AnimalPostResponse> create(
            @Valid @RequestBody CreateAnimalPostRequest request,
            @AuthenticationPrincipal User owner,
            UriComponentsBuilder uriBuilder
    );
}
