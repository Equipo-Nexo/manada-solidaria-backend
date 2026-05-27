package com.nexo.manada_solidaria_backend.services.interfaces;

import com.nexo.manada_solidaria_backend.models.DiffusionCampaign;

import java.util.List;

public interface DiffusionCampaignService {

    DiffusionCampaign create(DiffusionCampaign diffusionCampaign);

    List<DiffusionCampaign> getAll();

    DiffusionCampaign getById(Long id);

    DiffusionCampaign update(Long id, DiffusionCampaign diffusionCampaign);

    void delete(Long id);
}
