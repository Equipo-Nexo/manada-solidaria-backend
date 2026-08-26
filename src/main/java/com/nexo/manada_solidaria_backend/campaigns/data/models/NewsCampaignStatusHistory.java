package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignStatus;
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
public class NewsCampaignStatusHistory
        extends StatusHistory<NewsCampaignStatus> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_campaign_id", nullable = false)
    private NewsCampaign campaign;

    public NewsCampaignStatusHistory(
            NewsCampaignStatus status,
            NewsCampaign campaign
    ) {
        super(status);
        this.campaign = campaign;
    }
}
