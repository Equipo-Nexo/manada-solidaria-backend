package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.Optional;

public record OwnerResponse(
        String username,
        List<Rol> roles,
        String profileImageURL
) {

    public static OwnerResponse from(User owner) {
        return Optional.ofNullable(owner)
                .map(OwnerResponse::fromProfile)
                .orElse(null);
    }

    private static OwnerResponse fromProfile(User owner) {
        Profile profile = owner.getProfile();
        return new OwnerResponse(
                owner.getUsername(),
                profile.getRoles(),
                profile.getProfileImageURL()
        );
    }
}
