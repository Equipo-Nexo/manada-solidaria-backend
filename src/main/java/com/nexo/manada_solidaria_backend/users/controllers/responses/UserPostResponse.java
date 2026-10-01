package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.common.controllers.responses.LocationResponse;
import com.nexo.manada_solidaria_backend.common.controllers.responses.PhoneNumberResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
public abstract class UserPostResponse {

    public static final String ANIMAL = "animal";
    public static final String CAMPAIGN = "campaign";
    public static final String FUNDRAISING = "fundraising";

    private final UUID id;
    private final String title;
    private final String description;
    private final long createdSince;
    private final String imageId;
    private final String postType;
    private final String status;
    private final String type;
    private final LocationResponse location;
    private final PhoneNumberResponse phoneNumber;
    private final UUID ownerId;
    private final LocalDateTime createdAt;

    protected UserPostResponse(
            UUID id,
            String title,
            String description,
            LocalDateTime createdAt,
            String imageId,
            String postType,
            String status,
            String type,
            LocationResponse location,
            PhoneNumberResponse phoneNumber,
            UUID ownerId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdSince = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        this.imageId = imageId;
        this.postType = postType;
        this.status = status;
        this.type = type;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }
}
