package com.nexo.manada_solidaria_backend.users.controllers.responses;

public record UserMetricsResponse(
        long totalPosts,
        long happyCases
) {
}
