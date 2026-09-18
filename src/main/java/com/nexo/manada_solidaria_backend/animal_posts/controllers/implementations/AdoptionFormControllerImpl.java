package com.nexo.manada_solidaria_backend.animal_posts.controllers.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.interfaces.AdoptionFormController;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AdoptionFormResponse;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AdoptionFormService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AdoptionFormControllerImpl implements AdoptionFormController {

    private final AdoptionFormService adoptionFormService;

    @Override
    public AdoptionFormResponse createForm(CreateAdoptionFormRequest request, User authenticatedUser) {
        return adoptionFormService.createForm(request, authenticatedUser);
    }
}