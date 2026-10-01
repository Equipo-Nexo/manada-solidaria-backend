package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse.AnimalResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.OwnerResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AnimalUserPostResponse extends UserPostResponse {

    private final AnimalResponse animal;
    private final OwnerResponse owner;
    private final BigDecimal reward;

    public AnimalUserPostResponse(AnimalPostResponse animalPostResponse) {
        super(
                animalPostResponse.id(),
                animalPostResponse.name(),
                animalPostResponse.description(),
                animalPostResponse.createdAt(),
                animalPostResponse.imageUrl(),
                ANIMAL,
                animalPostResponse.status(),
                animalPostResponse.type().name(),
                animalPostResponse.location(),
                animalPostResponse.phoneNumber(),
                animalPostResponse.ownerId()
        );
        this.animal = animalPostResponse.animal();
        this.owner = animalPostResponse.owner();
        this.reward = animalPostResponse.reward();
    }
}
