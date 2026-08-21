package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;

public record UserResponse(
        String username,
        List<Rol> roles,
        String phoneNumber,
        String profileImageURL
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getProfile().getRoles(),
                user.getProfile().getPhoneNumber(),
                user.getProfile().getProfileImageURL()
        );
    }
}
