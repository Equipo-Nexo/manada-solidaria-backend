package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalUserPostResponse extends UserPostResponse {
    String status;

    public AnimalUserPostResponse(AnimalPostResponse animalPostResponse) {
        super(animalPostResponse.id(), animalPostResponse.name(), animalPostResponse.createdAt(), animalPostResponse.imageUrl(), "animal");
        this.status = animalPostResponse.status();
    }
}
