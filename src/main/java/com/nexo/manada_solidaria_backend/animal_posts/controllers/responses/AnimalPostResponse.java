package com.nexo.manada_solidaria_backend.animal_posts.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;

import java.time.LocalDateTime;
import java.util.UUID;


public record AnimalPostResponse(
        UUID id,
        AnimalPostType type,
        String title,
        String description,
        String imageUrl,
        AnimalResponse animal,
        LocationResponse location,
        String status,
        LocalDateTime createdAt,
        UUID ownerId
) {

    public static AnimalPostResponse from(AnimalPost post, String status) {
        return new AnimalPostResponse(
                post.getId(),
                post instanceof LostPost ? AnimalPostType.LOST : AnimalPostType.ADOPTION,
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                AnimalResponse.from(post.getAnimal()),
                LocationResponse.from(post.getLocation()),
                status,
                post.getCreatedAt(),
                post.getOwner() == null ? null : post.getOwner().getId()
        );
    }

    public record AnimalResponse(
            UUID id,
            AnimalType type,
            AnimalSize size,
            AnimalGender gender,
            String color,
            String breed,
            String fur,
            String age,
            String description
    ) {
        static AnimalResponse from(Animal animal) {
            return new AnimalResponse(
                    animal.getId(),
                    animal.getType(),
                    animal.getSize(),
                    animal.getGender(),
                    animal.getColor(),
                    animal.getBreed(),
                    animal.getFur(),
                    animal.getAge(),
                    animal.getDescription()
            );
        }
    }

    public record LocationResponse(
            UUID id,
            String name,
            String address,
            int number,
            double latitude,
            double longitude
    ) {
        static LocationResponse from(Location location) {
            return new LocationResponse(
                    location.getId(),
                    location.getName(),
                    location.getAddress(),
                    location.getNumber(),
                    location.getLatitude(),
                    location.getLongitude()
            );
        }
    }
}
