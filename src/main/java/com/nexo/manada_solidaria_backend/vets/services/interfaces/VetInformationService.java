package com.nexo.manada_solidaria_backend.vets.services.interfaces;

import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;

import java.util.List;

public interface VetInformationService {

    VetInformationResponse create(CreateVetInformationRequest request);

    List<VetInformationResponse> getAll();

}