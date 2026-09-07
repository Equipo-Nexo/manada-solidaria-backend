package com.nexo.manada_solidaria_backend.animal_posts.services.interfaces;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface AdoptionFormService {
    AdoptionFormResponse createForm(CreateAdoptionFormRequest request, User applicant);
}
