package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Campaign<
        STATUS extends Enum<STATUS>,
        HISTORY extends StatusHistory<STATUS>
        > {
    @Column(length = 50, nullable = false)
    private String title;
    private String description;
    private String imageId;
    private String shareCampaignUrl;
    private String phoneNumber;
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
            String phoneNumber,
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
        this.phoneNumber = request.phoneNumber();
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

    public void transitionTo(String targetStatus) {
        STATUS target = EnumUtils.parseOrThrow(
                statusType(),
                targetStatus
        );

        HISTORY current = getCurrentStatus();

        if (!isTransitionAllowed(current.getStatus(), target)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede pasar de "
                            + current.getStatus()
                            + " a "
                            + target
            );
        }

        current.finish();
        addStatus(target);
    }

    protected abstract Class<STATUS> statusType();

    protected abstract boolean isTransitionAllowed(
            STATUS current,
            STATUS target
    );

    protected abstract void addStatus(STATUS status);

    public abstract HISTORY getCurrentStatus();

    public abstract boolean isFinished();

    public abstract CampaignType getCampaignType();

}
