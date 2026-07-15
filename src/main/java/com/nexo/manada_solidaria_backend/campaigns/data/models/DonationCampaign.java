package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignStatus;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationCampaign extends Campaign<DonationCampaignStatusHistory> {
    private Long amountToBeCollected;
    private long amountCollected;
    private LocalDate endDate;
    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DonationCampaignStatusHistory> statusHistory;

    public DonationCampaign(
            String title,
            String description,
            String imageId,
            String shareCampaignUrl,
            Location location,
            User owner,
            Long amountToBeCollected,
            LocalDate campaignEndDate
    ) {
        super(title, description, imageId, shareCampaignUrl, location, owner);

        this.amountToBeCollected = amountToBeCollected;
        this.amountCollected = 0L;
        this.endDate = campaignEndDate;
        this.statusHistory = new ArrayList<>(
                List.of(new DonationCampaignStatusHistory(DonationCampaignStatus.CREATED, this))
        );
    }

    @Override
    public DonationCampaignStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(statusHistory);
    }

    @Override
    public boolean isFinished() {
        DonationCampaignStatus status = getCurrentStatus().getStatus();
        return status == DonationCampaignStatus.FINISHED || status == DonationCampaignStatus.COMPLETED;
    }
}
