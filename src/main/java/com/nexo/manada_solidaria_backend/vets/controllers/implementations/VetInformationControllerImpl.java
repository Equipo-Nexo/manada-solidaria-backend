package com.nexo.manada_solidaria_backend.vets.controllers.implementations;

import com.nexo.manada_solidaria_backend.vets.controllers.interfaces.VetInformationController;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class VetInformationControllerImpl implements VetInformationController {

    private final VetInformationService service;

    @Override
    public VetInformationResponse create(CreateVetInformationRequest request) {
        return service.create(request);
    }

    @Override
    public List<VetInformationResponse> getAll() {
        return service.getAll();
    }

    @Override
    public VetInformationResponse getById(UUID vetId) {
        return service.getById(vetId);
    }

    @Override
    public void delete(UUID vetId) { service.delete(vetId); }

    @Override
    public VetInformationResponse update(UUID vetId, UpdateVetInformationRequest request) {
        return service.update(vetId, request);
    }

}
