package com.nexo.manada_solidaria_backend.campaigns.data.repositories;

import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
}
