package com.nexo.manada_solidaria_backend.vets.controllers.interfaces;

import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/vets-information")
public interface VetInformationController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    VetInformationResponse create(
            @RequestBody @Valid CreateVetInformationRequest request
    );

}