package com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/animal-posts")
public interface AnimalPostController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AnimalPostResponse create(
            @Valid @RequestBody CreateAnimalPostRequest request,
            @AuthenticationPrincipal User owner
    );

    @GetMapping
    Page<AnimalPostResponse> getAnimalPosts(
            @RequestParam(required = false) AnimalPostType type,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable
    );
}
