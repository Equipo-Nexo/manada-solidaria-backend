package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.UUID;

public interface AdoptionFormService {
    AdoptionFormResponse createForm(CreateAdoptionFormRequest request, User applicant);

    List<AdoptionFormResponse> getFormsByPostId(UUID postId, User authenticatedUser);
}
