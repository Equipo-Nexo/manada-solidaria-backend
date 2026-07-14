package com.nexo.manada_solidaria_backend.users.controllers.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public abstract class UserPostResponse {
    String title;
    long createdSince;

    protected UserPostResponse(String title, LocalDateTime createdAt) {
        this.title = title;
        this.createdSince = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }
}

