package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationItem {
    private String name;
    private boolean isCompleted;
    @Enumerated(EnumType.STRING)
    private DonationCampaignCategory category;
    @ManyToOne
    @JoinColumn(name = "donation_campaign_id")
    private DonationCampaign campaign;
    @Id
    private final UUID id = UUID.randomUUID();

    public DonationItem(String name, DonationCampaignCategory category) {
        this.name = name;
        this.category = category;
        this.isCompleted = false;
    }
}
