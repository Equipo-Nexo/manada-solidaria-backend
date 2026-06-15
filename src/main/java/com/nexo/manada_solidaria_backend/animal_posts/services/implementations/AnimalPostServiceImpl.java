package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.components.AnimalPostFactory;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.LostPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnimalPostServiceImpl implements AnimalPostService {

    private final AnimalPostRepository animalPostRepository;
    private final LostPostRepository lostPostRepository;
    private final AdoptionPostRepository adoptionPostRepository;
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
    public Page<AnimalPostResponse> findAll(AnimalPostFilter type, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        // Cada valor del filtro mapea a un unico subtipo:
        // los estados de lost solo consultan a LostPost y los de adoption solo AdoptionPost
        Page<? extends AnimalPost> posts = type == null
                ? animalPostRepository.findAll(sorted)
                : switch (type) {
                    case LOST -> lostPostRepository.findAll(sorted);
                    case SEARCHING -> lostPostRepository.findAllByCurrentStatus(StatusLostPost.SEARCHING, sorted);
                    case FOUND -> lostPostRepository.findAllByCurrentStatus(StatusLostPost.FOUND, sorted);
                    case ADOPTION -> adoptionPostRepository.findAll(sorted);
                    case CREATED -> adoptionPostRepository.findAllByCurrentStatus(StatusAdoptionPost.CREATED, sorted);
                    case SEARCHING_ADOPT_AND_TRANSIT ->
                            adoptionPostRepository.findAllByCurrentStatus(StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT, sorted);
                    case SEARCHING_ADOPT ->
                            adoptionPostRepository.findAllByCurrentStatus(StatusAdoptionPost.SEARCHING_ADOPT, sorted);
                    case ADOPTED -> adoptionPostRepository.findAllByCurrentStatus(StatusAdoptionPost.ADOPTED, sorted);
                };
        return posts.map(AnimalPostResponse::from);
    }
}
