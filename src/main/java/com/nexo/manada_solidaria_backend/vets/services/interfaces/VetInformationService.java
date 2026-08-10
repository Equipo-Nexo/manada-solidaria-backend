package com.nexo.manada_solidaria_backend.vets.services.interfaces;

import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;

import java.util.List;

import java.util.UUID;

public interface VetInformationService {

    VetInformationResponse create(CreateVetInformationRequest request);

    List<VetInformationResponse> getAll();

    VetInformationResponse getById(UUID vetId);

    void delete(UUID vetId);

    VetInformationResponse update(UUID vetId, UpdateVetInformationRequest request);
}