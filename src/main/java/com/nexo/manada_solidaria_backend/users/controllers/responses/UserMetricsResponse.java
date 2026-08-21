package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record UserMetricsResponse(
        long totalPosts,
        long completedPosts,
        long daysSinceRegistration
) {

    public static UserMetricsResponse from(User user, long totalPosts, long completedPosts) {
        return new UserMetricsResponse(
                totalPosts,
                completedPosts,
                ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now())
        );
    }
}
