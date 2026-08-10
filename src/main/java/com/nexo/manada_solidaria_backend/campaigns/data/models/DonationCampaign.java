package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignStatus;
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
    private LocalDate campaignEndDate;
    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DonationItem> items;
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
            String phoneNumber,
            Location location,
            User owner,
            LocalDate campaignEndDate
    ) {
        super(title, description, imageId, shareCampaignUrl, phoneNumber, location, owner);

        this.campaignEndDate = campaignEndDate;
        this.statusHistory = new ArrayList<>(
                List.of(new DonationCampaignStatusHistory(CampaignStatus.CREATED, this))
        );
        this.items = new ArrayList<>();
    }

    @Override
    public void update(UpdateCampaignRequest request) {
        super.update(request);

        this.campaignEndDate = request.campaignEndDate();
    }

    @Override
    public DonationCampaignStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(statusHistory);
    }

    @Override
    public boolean isFinished() {
        CampaignStatus status = getCurrentStatus().getStatus();
        return status == CampaignStatus.FINISHED || status == CampaignStatus.COMPLETED;
    }

    public void addItem(DonationItem item) {
        if (item != null) {
            this.items.add(item);
            item.setCampaign(this);
        }
    }

    @Override
    public CampaignType getCampaignType() {
        return CampaignType.DONATION;
    }
}