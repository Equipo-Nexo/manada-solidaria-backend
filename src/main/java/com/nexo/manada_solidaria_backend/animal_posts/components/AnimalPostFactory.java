package com.nexo.manada_solidaria_backend.animal_posts.components;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.stereotype.Component;

@Component
public class AnimalPostFactory {

    public AnimalPost buildAnimalPost(CreateAnimalPostRequest request, User owner) {
        Animal animal = buildAnimal(request.animal());
        Location location = buildLocation(request.location());
        return switch (request.type()) {
            case LOST -> buildLostPost(request, animal, location, owner);
            case ADOPTION -> buildAdoptionPost(request, animal, location, owner);
        };
    }

    private Animal buildAnimal(CreateAnimalPostRequest.AnimalRequest req) {
        return new Animal(
                req.color(),
                req.age(),
                req.size(),
                req.gender(),
                req.type()
        );
    }

    private Location buildLocation(CreateAnimalPostRequest.LocationRequest req) {
        return new Location(
                req.name(),
                req.address(),
                req.number(),
                req.latitude(),
                req.longitude()
        );
    }

    private LostPost buildLostPost(CreateAnimalPostRequest request, Animal animal, Location location, User owner) {
        return new LostPost(
                request.name(),
                request.description(),
                request.imageId(),
                null, //TODO definir una nueva card en jira nos limitamos al post en ms-30
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                request.hasOwner(),
                owner,
                location,
                animal,
                request.reward()
        );
    }

    private AdoptionPost buildAdoptionPost(CreateAnimalPostRequest request, Animal animal, Location location, User owner) {
        return new AdoptionPost(
                request.name(),
                request.description(),
                request.imageId(),
                null, //TODO definir una nueva card en jira nos limitamos al post en ms-30
                PhoneNumberRequest.toDomain(request.phoneNumber()),
                owner,
                animal,
                location,
                Boolean.TRUE.equals(request.inTransit())
        );
    }
}
