package com.nexo.manada_solidaria_backend.controllers.interfaces;

import com.nexo.manada_solidaria_backend.models.DiffusionCampaign;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/diffusion-campaigns")
public interface DiffusionCampaignController {

    @PostMapping
    ResponseEntity<DiffusionCampaign> create(@RequestBody DiffusionCampaign diffusionCampaign);

    @GetMapping
    ResponseEntity<List<DiffusionCampaign>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<DiffusionCampaign> getById(@PathVariable Long id);

    @PutMapping("/{id}")
    ResponseEntity<DiffusionCampaign> update(@PathVariable Long id, @RequestBody DiffusionCampaign diffusionCampaign);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
