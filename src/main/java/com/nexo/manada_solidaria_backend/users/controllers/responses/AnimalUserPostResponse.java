package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalUserPostResponse extends UserPostResponse {
    String status;

    public AnimalUserPostResponse(AnimalPostResponse animalPostResponse) {
        super(animalPostResponse.title(), animalPostResponse.createdAt());
        this.status = animalPostResponse.status();
    }
}
