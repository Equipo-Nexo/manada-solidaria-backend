package com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/adoption-forms")
public interface AdoptionFormController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AdoptionFormResponse createForm(
            @Valid @RequestBody CreateAdoptionFormRequest request,
            @AuthenticationPrincipal User authenticatedUser
    );

    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    List<AdoptionFormResponse> getFormsByPostId(
            @PathVariable("postId") UUID postId,
            @AuthenticationPrincipal User authenticatedUser
    );
}