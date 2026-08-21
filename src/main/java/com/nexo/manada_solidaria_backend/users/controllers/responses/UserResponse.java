package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.common.controllers.responses.PhoneNumberResponse;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        List<Rol> roles,
        PhoneNumberResponse phoneNumber,
        String profileImageURL
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getProfile().getRoles(),
                PhoneNumberResponse.from(user.getProfile().getPhoneNumber()),
                user.getProfile().getProfileImageURL()
        );
    }
}
