package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.UUID;

public record UserDetailResponse(
        UUID id,
        String username,
        ProfileResponse profile,
        List<Rol> roles,
        List<UserPostResponse> posts
) {

    public static UserDetailResponse from(User user, List<UserPostResponse> posts) {
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                ProfileResponse.from(user.getProfile()),
                user.getProfile().getRoles(),
                posts
        );
    }
}
