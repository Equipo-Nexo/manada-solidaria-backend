package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaginStatus;
import com.nexo.manada_solidaria_backend.common.utils.StatusHistoryUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsCampaign extends Campaign<NewsCampaignStatusHistory> {

    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NewsCampaignStatusHistory> statusHistory;
    @Override
    public NewsCampaignStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(statusHistory);
    }

    public NewsCampaign(
            String title,
            String description,
            String imageId,
            String shareCampaignUrl,
            Location location,
            User owner,
            List<NewsCampaignStatusHistory> statusHistory
    ) {
        super(
                title,
                description,
                imageId,
                shareCampaignUrl,
                location,
                owner
        );

        this.statusHistory = statusHistory;
    }
}
