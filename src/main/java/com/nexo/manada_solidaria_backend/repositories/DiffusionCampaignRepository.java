package com.nexo.manada_solidaria_backend.repositories;

import com.nexo.manada_solidaria_backend.models.DiffusionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiffusionCampaignRepository extends JpaRepository<DiffusionCampaign, Long> {
}
