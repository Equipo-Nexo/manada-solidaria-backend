package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationFundraisingCampaignStatus;
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
public class FundraisingCampaign extends Campaign<DonationFundraisingCampaignStatus, FundraisingCampaignStatusHistory> {
    private String accountAlias;
    private Long amountToBeCollected;
    private Long amountCollected;
    private LocalDate campaignEndDate;
    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FundraisingCampaignStatusHistory> statusHistory;

    public FundraisingCampaign(
            String title,
            String description,
            String imageId,
            String shareCampaignUrl,
            String phoneNumber,
            Location location,
            User owner,
            String accountAlias,
            Long amountToBeCollected,
            LocalDate campaignEndDate
    ) {
        super(title, description, imageId, shareCampaignUrl, phoneNumber, location, owner);

        this.accountAlias = accountAlias;
        this.amountToBeCollected = amountToBeCollected;
        this.amountCollected = 0L;
        this.campaignEndDate = campaignEndDate;
        this.statusHistory = new ArrayList<>(
                List.of(new FundraisingCampaignStatusHistory(DonationFundraisingCampaignStatus.CREATED, this))
        );
    }

    @Override
    public void update(UpdateCampaignRequest request) {
        super.update(request);

        this.accountAlias = request.accountAlias();
        this.amountToBeCollected = request.amountToBeCollected();
        this.amountCollected = request.amountCollected();
        this.campaignEndDate = request.campaignEndDate();
    }

    @Override
    public FundraisingCampaignStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(statusHistory);
    }

    @Override
    public boolean isFinished() {
        DonationFundraisingCampaignStatus status = getCurrentStatus().getStatus();
        return status == DonationFundraisingCampaignStatus.FINISHED || status == DonationFundraisingCampaignStatus.COMPLETED;
    }

    @Override
    public CampaignType getCampaignType() {
        return CampaignType.FUNDRAISING;
    }

    @Override
    protected Class<DonationFundraisingCampaignStatus> statusType() {
        return DonationFundraisingCampaignStatus.class;
    }

    @Override
    protected boolean isTransitionAllowed(
            DonationFundraisingCampaignStatus current,
            DonationFundraisingCampaignStatus target
    ) {
        return switch (current) {
            case CREATED ->
                    target == DonationFundraisingCampaignStatus.COMPLETED;

            case COMPLETED ->
                    target == DonationFundraisingCampaignStatus.FINISHED;

            case FINISHED ->
                    false;
        };
    }

    @Override
    protected void addStatus(
            DonationFundraisingCampaignStatus status
    ) {
        statusHistory.add(
                new FundraisingCampaignStatusHistory(
                        status,
                        this
                )
        );
    }
}
