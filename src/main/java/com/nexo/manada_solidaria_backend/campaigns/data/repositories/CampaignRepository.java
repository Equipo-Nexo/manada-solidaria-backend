package com.nexo.manada_solidaria_backend.campaigns.data.repositories;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaginStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

    @Query("""
        SELECT c
        FROM Campaign c
        WHERE TYPE(c) <> FundraisingCampaign
    """)
    Page<Campaign<?>> findCampaigns(Pageable pageable);

    @Query("""
        SELECT c
        FROM DonationCampaign c
    """)
    Page<Campaign<?>> findDonationCampaigns(Pageable pageable);

    @Query("""
        SELECT c
        FROM NewsCampaign c
        WHERE c.category = :category
    """)
    Page<Campaign<?>> findNewsCampaignsByCategory(
            @Param("category") NewsCampaignCategory category,
            Pageable pageable
    );

    @Query("""
        SELECT c
        FROM FundraisingCampaign c
    """)
    Page<Campaign<?>> findFundraisingCampaigns(Pageable pageable);

    @Query("""
        SELECT c
        FROM Campaign c
        WHERE TYPE(c) <> FundraisingCampaign
        AND c.owner = :owner
    """)
    List<Campaign<?>> findCampaignsByOwner(@Param("owner") User user);

    @Query("""
        SELECT c
        FROM Campaign c
        WHERE TYPE(c) = FundraisingCampaign
        AND c.owner = :owner
    """)
    List<Campaign<?>> findFundraisingCampaignsByOwner(@Param("owner") User user);

    @Query("""
        SELECT count(c) FROM Campaign c
        WHERE c.owner = :owner
          AND (EXISTS (SELECT 1 FROM DonationCampaignStatusHistory h
                       WHERE h.campaign.id = c.id AND h.finishedAt IS NULL
                         AND h.status IN :campaignStatuses)
            OR EXISTS (SELECT 1 FROM FundraisingCampaignStatusHistory h
                       WHERE h.campaign.id = c.id AND h.finishedAt IS NULL
                         AND h.status IN :campaignStatuses)
            OR EXISTS (SELECT 1 FROM NewsCampaignStatusHistory h
                       WHERE h.campaign.id = c.id AND h.finishedAt IS NULL
                         AND h.status IN :newsStatuses))
    """)
    long countFinishedByOwner(
            @Param("owner") User owner,
            @Param("campaignStatuses") Set<CampaignStatus> campaignStatuses,
            @Param("newsStatuses") Set<NewsCampaginStatus> newsStatuses
    );

    long countByOwner(User owner);

}
