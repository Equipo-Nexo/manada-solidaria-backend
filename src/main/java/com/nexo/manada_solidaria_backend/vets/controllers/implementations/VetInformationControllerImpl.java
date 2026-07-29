package com.nexo.manada_solidaria_backend.vets.controllers.implementations;

import com.nexo.manada_solidaria_backend.vets.controllers.interfaces.VetInformationController;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class VetInformationControllerImpl implements VetInformationController {

    private final VetInformationService service;

    @Override
    public VetInformationResponse create(CreateVetInformationRequest request) {
        return service.create(request);
    }
}
