package com.nexo.manada_solidaria_backend.animal_post.controllers.implementations;

import com.nexo.manada_solidaria_backend.animal_post.controllers.interfaces.AnimalPostController;
import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_post.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_post.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@AllArgsConstructor
public class AnimalPostControllerImpl implements AnimalPostController {

    private final AnimalPostService animalPostService;

    @Override
    public ResponseEntity<AnimalPostResponse> create(
            CreateAnimalPostRequest request,
            User owner,
            UriComponentsBuilder uriBuilder
    ) {
        AnimalPostResponse response = animalPostService.create(request, owner);
        URI location = uriBuilder.path("/animal-post/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<PagedModel<AnimalPostResponse>> findAll(AnimalPostType type, Pageable pageable) {
        Page<AnimalPostResponse> page = animalPostService.findAll(type, pageable);
        return ResponseEntity.ok(new PagedModel<>(page));
    }
}
