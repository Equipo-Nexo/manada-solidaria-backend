package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalAge;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;

public record GetAnimalPostsRequest(
        AnimalPostFilter type,
        String status,
        AnimalType animalType,
        AnimalSize animalSize,
        AnimalGender animalGender,
        AnimalAge animalAge,
        String animalColor
) {
}
