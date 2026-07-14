package com.nexo.manada_solidaria_backend.users.controllers.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
public abstract class UserPostResponse {

    private final UUID id;
    private final String title;
    private final long createdSince;

    protected UserPostResponse(UUID id, String title, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.createdSince = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }
}

