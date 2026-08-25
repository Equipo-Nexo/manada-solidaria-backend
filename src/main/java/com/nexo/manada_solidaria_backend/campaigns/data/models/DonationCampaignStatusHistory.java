package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationFundraisingCampaignStatus;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@NoArgsConstructor
public class DonationCampaignStatusHistory extends StatusHistory<DonationFundraisingCampaignStatus> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_campaign_id", nullable = false)
    private DonationCampaign campaign;

    public DonationCampaignStatusHistory(
            DonationFundraisingCampaignStatus status,
            DonationCampaign campaign
    ) {
        super(status);
        this.campaign = campaign;
    }
}
