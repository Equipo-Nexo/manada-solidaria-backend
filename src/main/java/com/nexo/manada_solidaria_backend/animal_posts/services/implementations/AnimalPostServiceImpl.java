package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.components.AnimalPostFactory;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnimalPostServiceImpl implements AnimalPostService {

    private final AnimalPostRepository animalPostRepository;
    private final AnimalPostFactory animalPostFactory;

    @Override
    public AnimalPostResponse create(CreateAnimalPostRequest request, User owner) {
        AnimalPost saved = animalPostRepository.save(
                animalPostFactory.buildAnimalPost(request, owner)
        );
        return AnimalPostResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalPostResponse> getAnimalPosts(AnimalPostType type, String status, Pageable pageable) {
        return animalPostRepository
                .findAllFiltered(EnumUtils.getNameOrNull(type), status, pageable)
                .map(AnimalPostResponse::from);
    }
}
