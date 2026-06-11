package com.nexo.manada_solidaria_backend.animal_post.controllers.interfaces;

import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_post.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

public interface AnimalPostController {

    @PostMapping("/animal-post")
    ResponseEntity<AnimalPostResponse> create(
            @Valid @RequestBody CreateAnimalPostRequest request,
            @AuthenticationPrincipal User owner,
            UriComponentsBuilder uriBuilder
    );

    @GetMapping("/animal-posts")
    ResponseEntity<PagedModel<AnimalPostResponse>> findAll(
            @RequestParam(required = false) AnimalPostType type,
            @ParameterObject Pageable pageable
    );
}
