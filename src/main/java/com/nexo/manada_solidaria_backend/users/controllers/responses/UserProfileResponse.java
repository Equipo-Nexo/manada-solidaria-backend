package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        ProfileResponse profile,
        Set<Rol> roles,
        LocalDateTime createdAt
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                ProfileResponse.from(user.getProfile()),
                user.getProfile().getRoles(),
                user.getCreatedAt()
        );
    }
}
