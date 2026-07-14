package com.nexo.manada_solidaria_backend.campaigns.data.repositories;

import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

    @Query("SELECT c FROM Campaign c WHERE :type IS NULL " +
            "OR (:type = 'DONATION' AND TYPE(c) = DonationCampaign) " +
            "OR (:type = 'NEWS' AND TYPE(c) = NewsCampaign)")
    Page<Campaign<?>> findAllFiltered(@Param("type") String type, Pageable pageable);

    List<Campaign<?>> findAllByOwner(User user);
}
