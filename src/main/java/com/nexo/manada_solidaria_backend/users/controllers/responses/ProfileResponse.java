package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.models.Profile;

public record ProfileResponse(
        String name,
        String lastname,
        String email,
        String phoneNumber,
        String profileImageURL
) {

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getName(),
                profile.getLastname(),
                profile.getEmail(),
                profile.getPhoneNumber(),
                profile.getProfileImageURL()
        );
    }
}
