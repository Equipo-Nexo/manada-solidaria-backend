package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.components.AnimalPostFactory;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.GetAnimalPostsRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.TransitionStatusRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.HappyCaseResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AnimalPostServiceImpl implements AnimalPostService {

    private static final int RECENT_DAYS = 7;

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
    public Page<AnimalPostResponse> getAnimalPosts(GetAnimalPostsRequest request, Pageable pageable) {
        return animalPostRepository
                .findAllFiltered(
                        EnumUtils.getNameOrNull(request.type()),
                        request.status(),
                        request.animalType(),
                        request.animalSize(),
                        request.animalGender(),
                        request.animalAge(),
                        request.animalColor(),
                        pageable
                )
                .map(AnimalPostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalPostResponse getAnimalPost(UUID animalPostId) {
        return AnimalPostResponse.from(getAnimalPostOrThrow(animalPostId));
    }

    @Override
    public void update(UUID animalPostId, UpdateAnimalPostRequest request, User authenticatedUser) {
        AnimalPost post = getAnimalPostOrThrow(animalPostId);

        validateOwner(post, authenticatedUser);

        post.update(request);
        animalPostRepository.save(post);
    }

    @Override
    @Transactional
    public AnimalPostResponse transitionStatus(UUID animalPostId, TransitionStatusRequest request, User authenticatedUser) {
        AnimalPost post = getAnimalPostOrThrow(animalPostId);

        validateOwner(post, authenticatedUser);
        post.transitionTo(request.status());

        return AnimalPostResponse.from(animalPostRepository.save(post));
    }

    @Override
    public void delete(UUID animalPostId, User authenticatedUser) {
        AnimalPost post = getAnimalPostOrThrow(animalPostId);

        validateOwner(post, authenticatedUser);

        animalPostRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HappyCaseResponse> getHappyCases(Pageable pageable) {
        return animalPostRepository.findHappyCases(LostPost.HAPPY_STATUSES, AdoptionPost.HAPPY_STATUSES, pageable)
                .map(post -> HappyCaseResponse.from(post, isRecentlyResolved(post)));
    }

    private static boolean isRecentlyResolved(AnimalPost<?, ?> post) {
        return post.getCurrentStatus().getCreatedAt().toLocalDate()
                .isAfter(LocalDate.now().minusDays(RECENT_DAYS));
    }

    @Override
    public List<AnimalPostResponse> getUserAnimalPosts(User user) {
        return animalPostRepository.findAllByOwner(user)
                .stream()
                .map(AnimalPostResponse::from)
                .toList();
    }

    private void validateOwner(AnimalPost post, User authenticatedUser) {
        if (!post.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el dueño puede modificar la publicación"
            );
        }
    }

    private AnimalPost getAnimalPostOrThrow(UUID animalPostId) {
        return animalPostRepository.findById(animalPostId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La publicación no existe"
                        ));
    }
}
