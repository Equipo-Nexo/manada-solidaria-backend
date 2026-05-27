package com.nexo.manada_solidaria_backend.services.implementations;

import com.nexo.manada_solidaria_backend.models.DiffusionCampaign;
import com.nexo.manada_solidaria_backend.repositories.DiffusionCampaignRepository;
import com.nexo.manada_solidaria_backend.services.interfaces.DiffusionCampaignService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiffusionCampaignServiceImpl implements DiffusionCampaignService {

    private final DiffusionCampaignRepository diffusionCampaignRepository;

    @Override
    public DiffusionCampaign create(DiffusionCampaign diffusionCampaign) {
        return diffusionCampaignRepository.save(diffusionCampaign);
    }

    @Override
    public List<DiffusionCampaign> getAll() {
        return diffusionCampaignRepository.findAll();
    }

    @Override
    public DiffusionCampaign getById(Long id) {
        return diffusionCampaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiffusionCampaign not found with id: " + id));
    }

    @Override
    public DiffusionCampaign update(Long id, DiffusionCampaign diffusionCampaign) {
        DiffusionCampaign existing = getById(id);
        existing.setTitle(diffusionCampaign.getTitle());
        existing.setDescription(diffusionCampaign.getDescription());
        existing.setOwner(diffusionCampaign.getOwner());
        existing.setEndDate(diffusionCampaign.getEndDate());
        return diffusionCampaignRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        DiffusionCampaign existing = getById(id);
        diffusionCampaignRepository.delete(existing);
    }
}
