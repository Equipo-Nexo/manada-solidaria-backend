package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignStatus;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DonationCampaignStatusHistory extends StatusHistory<DonationCampaignStatus> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_campaign_id", nullable = false)
    private DonationCampaign campaign;

    public DonationCampaignStatusHistory(DonationCampaignStatus status) {
        super(status);
    }
}
