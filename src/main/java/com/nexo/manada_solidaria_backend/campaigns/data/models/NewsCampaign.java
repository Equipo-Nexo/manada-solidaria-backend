package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.utils.StatusHistoryUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsCampaign extends Campaign<NewsCampaignStatus, NewsCampaignStatusHistory> {
    private LocalDateTime newsStartDateTime;
    private LocalDateTime newsEndDateTime;
    @Enumerated(EnumType.STRING)
    private NewsCampaignCategory category;

    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NewsCampaignStatusHistory> statusHistory;

    public NewsCampaign(
            String title,
            String description,
            String imageId,
            String shareCampaignUrl,
            PhoneNumber phoneNumber,
            Location location,
            User owner,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            NewsCampaignCategory category
    ) {
        super(title, description, imageId, shareCampaignUrl, phoneNumber, location, owner);

        this.newsStartDateTime = startDateTime != null
                ? startDateTime
                : LocalDateTime.now();

        this.newsEndDateTime = endDateTime;
        this.category = category;

        NewsCampaignStatus initialStatus =
                startDateTime == null
                        ? NewsCampaignStatus.STARTED
                        : NewsCampaignStatus.CREATED;

        this.statusHistory = new ArrayList<>(
                List.of(new NewsCampaignStatusHistory(initialStatus, this))
        );
    }

    @Override
    public void update(UpdateCampaignRequest request) {
        super.update(request);

        this.newsStartDateTime = request.newsStartDateTime();
        this.newsEndDateTime = request.newsEndDateTime();
        this.category = request.category();
    }

    @Override
    public NewsCampaignStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(statusHistory);
    }

    @Override
    public boolean isFinished() {
        return getCurrentStatus().getStatus() == NewsCampaignStatus.FINISHED;
    }

    @Override
    public CampaignType getCampaignType() {
        return CampaignType.NEWS;
    }


    @Override
    protected Class<NewsCampaignStatus> statusType() {
        return NewsCampaignStatus.class;
    }

    @Override
    protected boolean isTransitionAllowed(
            NewsCampaignStatus current,
            NewsCampaignStatus target
    ) {
        return switch (current) {
            case CREATED ->
                    false;

            case STARTED ->
                    target == NewsCampaignStatus.FINISHED;

            case FINISHED ->
                    false;
        };
    }

    @Override
    protected void addStatus(NewsCampaignStatus status) {
        statusHistory.add(
                new NewsCampaignStatusHistory(
                        status,
                        this
                )
        );
    }

    @Override
    protected boolean isFinishedStatus(NewsCampaignStatus status) {
        return status == NewsCampaignStatus.FINISHED;
    }
}
