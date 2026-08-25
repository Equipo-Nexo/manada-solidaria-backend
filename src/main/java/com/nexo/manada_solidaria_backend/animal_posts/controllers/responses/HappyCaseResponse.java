package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record HappyCaseResponse(
        UUID id,
        String name,
        String description,
        String imageUrl,
        String status,
        boolean isRecent,
        OwnerResponse owner
) {

    public static HappyCaseResponse from(AnimalPost<?, ?> post, boolean isRecent) {
        return new HappyCaseResponse(
                post.getId(),
                post.getName(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCurrentStatus().getStatus().name(),
                isRecent,
                OwnerResponse.from(post.getOwner())
        );
    }

    public record OwnerResponse(
            String username,
            List<Rol> roles,
            String profileImageURL
    ) {

        static OwnerResponse from(User owner) {
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
}
