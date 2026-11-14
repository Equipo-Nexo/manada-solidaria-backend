package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse.AnimalResponse;
import com.nexo.manada_solidaria_backend.common.controllers.responses.OwnerResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AnimalUserPostResponse extends UserPostResponse {

    private final AnimalResponse animal;
    private final OwnerResponse owner;
    private final BigDecimal reward;
    private final long reactionsCount;
    private final boolean reactedByMe;
    private final Boolean hasCompletedAdoptionForm;

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
                animalPostResponse.ownerId(),
                animalPostResponse.owner()
        );
        this.animal = animalPostResponse.animal();
        this.owner = animalPostResponse.owner();
        this.reward = animalPostResponse.reward();
        this.reactionsCount = animalPostResponse.reactionsCount();
        this.reactedByMe = animalPostResponse.reactedByMe();
        this.hasCompletedAdoptionForm = animalPostResponse.hasCompletedAdoptionForm();
    }
}