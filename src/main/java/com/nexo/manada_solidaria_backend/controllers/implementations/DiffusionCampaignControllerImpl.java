package com.nexo.manada_solidaria_backend.controllers.implementations;

import com.nexo.manada_solidaria_backend.controllers.interfaces.DiffusionCampaignController;
import com.nexo.manada_solidaria_backend.models.DiffusionCampaign;
import com.nexo.manada_solidaria_backend.services.interfaces.DiffusionCampaignService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DiffusionCampaignControllerImpl implements DiffusionCampaignController {

    private final DiffusionCampaignService diffusionCampaignService;

    @Override
    public ResponseEntity<DiffusionCampaign> create(DiffusionCampaign diffusionCampaign) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diffusionCampaignService.create(diffusionCampaign));
    }

    @Override
    public ResponseEntity<List<DiffusionCampaign>> getAll() {
        return ResponseEntity.ok(diffusionCampaignService.getAll());
    }

    @Override
    public ResponseEntity<DiffusionCampaign> getById(Long id) {
        return ResponseEntity.ok(diffusionCampaignService.getById(id));
    }

    @Override
    public ResponseEntity<DiffusionCampaign> update(Long id, DiffusionCampaign diffusionCampaign) {
        return ResponseEntity.ok(diffusionCampaignService.update(id, diffusionCampaign));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        diffusionCampaignService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
