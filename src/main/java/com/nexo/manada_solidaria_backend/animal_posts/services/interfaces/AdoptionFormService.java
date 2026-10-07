package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormDetailResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormsWithCountersResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.FormFilter;
import com.nexo.manada_solidaria_backend.auth.components.pre_filters.AnimalPostOwner;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.UUID;

public interface AdoptionFormService {
    AdoptionFormResponse createForm(CreateAdoptionFormRequest request, User applicant);

    @AnimalPostOwner
    List<AdoptionFormResponse> getFormsByPostId(UUID animalPostId);

    AdoptionFormsWithCountersResponse getFormsByUser(UUID userId, FormFilter filter);

    AdoptionFormDetailResponse getFormById(UUID adoptionFormId, User authenticatedUser);
}
