package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public record UserDetailResponse(
        UUID id,
        String username,
        ProfileResponse profile,
        List<Rol> roles,
        List<UserPostResponse> posts,
        UserMetricsResponse metrics
) {

    public static UserDetailResponse from(User user, List<UserPostResponse> posts, long completedPosts) {
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                ProfileResponse.from(user.getProfile()),
                user.getProfile().getRoles(),
                posts,
                UserMetricsResponse.from(user, posts, completedPosts)
        );
    }

    public record UserMetricsResponse(
            long totalPosts,
            long completedPosts,
            long daysSinceRegistration
    ) {

        static UserMetricsResponse from(User user, List<UserPostResponse> posts, long completedPosts) {
            return new UserMetricsResponse(
                    posts.size(),
                    completedPosts,
                    ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now())
            );
        }
    }
}
