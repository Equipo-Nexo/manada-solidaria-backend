package com.nexo.manada_solidaria_backend.vets.controllers.interfaces;

import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;


@RequestMapping("/vets")
public interface VetInformationController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    VetInformationResponse create(
            @RequestBody @Valid CreateVetInformationRequest request
    );

    @GetMapping
    public List<VetInformationResponse> getAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, name = "open_only") Boolean openOnly,
            @RequestParam(required = false, name = "user_latitude") Double userLatitude,
            @RequestParam(required = false, name = "user_longitude") Double userLongitude
    );

    @GetMapping("/{vetId}")
    VetInformationResponse getById(
            @PathVariable UUID vetId
    );

    @DeleteMapping("/{vetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID vetId);
    @PutMapping("/{vetId}")
    VetInformationResponse update(
            @PathVariable UUID vetId,
            @RequestBody @Valid UpdateVetInformationRequest request
    );

}