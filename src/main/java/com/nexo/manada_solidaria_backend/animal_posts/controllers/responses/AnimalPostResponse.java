package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalAge;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.locations.controllers.responses.LocationResponse;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


public record AnimalPostResponse(
        UUID id,
        AnimalPostFilter type,
        String name,
        String description,
        String imageUrl,
        AnimalResponse animal,
        LocationResponse location,
        String status,
        LocalDateTime createdAt,
        UUID ownerId,
        String phoneNumber,
        BigDecimal reward
) {

    public static AnimalPostResponse from(AnimalPost post) {
        return new AnimalPostResponse(
                post.getId(),
                post.getType(),
                post.getName(),
                post.getDescription(),
                post.getImageUrl(),
                AnimalResponse.from(post.getAnimal()),
                LocationResponse.from(post.getLocation()),
                post.getCurrentStatus().getStatus().name(),
                post.getCreatedAt(),
                Optional.ofNullable(post.getOwner())
                        .map(User::getId)
                        .orElse(null),
                post.getPhoneNumber(),
                post instanceof LostPost lost ? lost.getReward() : null
        );
    }

    public record AnimalResponse(
            UUID id,
            AnimalType type,
            AnimalSize size,
            AnimalGender gender,
            String color,
            AnimalAge age
    ) {
        static AnimalResponse from(Animal animal) {
            return new AnimalResponse(
                    animal.getId(),
                    animal.getType(),
                    animal.getSize(),
                    animal.getGender(),
                    animal.getColor(),
                    animal.getAge()
            );
        }
    }
}
