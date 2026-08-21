package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Campaign<T extends StatusHistory<?>> {
    @Column(length = 50, nullable = false)
    private String title;
    private String description;
    private String imageId;
    private String shareCampaignUrl;
    @Embedded
    private PhoneNumber phoneNumber;
    private LocalDateTime updatedAt = null;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    private Location location;
    @ManyToOne(optional = false)
    private User owner;
    @Id
    private final UUID id = UUID.randomUUID();

    protected Campaign(
            String title,
            String description,
            String imageId,
            String shareCampaignUrl,
            PhoneNumber phoneNumber,
            Location location,
            User owner
    ) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.shareCampaignUrl = shareCampaignUrl;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.owner = owner;
    }

    public void update(UpdateCampaignRequest request) {
        this.title = request.title();
        this.description = request.description();
        this.imageId = request.imageId();
        this.phoneNumber = PhoneNumberRequest.toDomain(request.phoneNumber());
        this.updatedAt = LocalDateTime.now();
        updateLocation(request);
    }

    private void updateLocation(UpdateCampaignRequest request) {
        if (request.location() == null) {
            this.location = null;
            return;
        }

        if (this.location != null) {
            this.location.update(request.location());
        } else {
            this.location = request.location().toDomain();
        }
    }

    public abstract T getCurrentStatus();

    public abstract boolean isFinished();

    public abstract CampaignType getCampaignType();
}
