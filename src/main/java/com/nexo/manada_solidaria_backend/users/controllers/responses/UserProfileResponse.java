package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        ProfileResponse profile,
        List<Rol> roles,
        long daysSinceRegistration
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                ProfileResponse.from(user.getProfile()),
                user.getProfile().getRoles(),
                user.getDaysSinceRegistration()
        );
    }
}
