package com.nexo.manada_solidaria_backend.animal_posts.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.components.AnimalPostFactory;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.GetAnimalPostsRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.TransitionStatusRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.HappyCaseResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext.AnimalTraits;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AnimalPostServiceImpl implements AnimalPostService {

    private static final int RECENT_DAYS = 7;

    private final AnimalPostRepository animalPostRepository;
    private final AnimalPostFactory animalPostFactory;
    private final NotificationService notificationService;

    @Override
    public AnimalPostResponse create(CreateAnimalPostRequest request, User owner) {
        AnimalPost saved = animalPostRepository.save(
                animalPostFactory.buildAnimalPost(request, owner)
        );

        if (Boolean.TRUE.equals(request.needTransport())) {
            notificationService.notify(
                    NotificationType.NEW_CARRIAGE_REQUEST,
                    Map.of("postId", saved.getId()));
        }

        if (isStreetAnimal(saved)) {
            notificationService.notify(
                    NotificationType.SIMILAR_ANIMAL_RECENTLY_LOST,
                    Map.of("postId", saved.getId(), "location", describe(saved.getLocation())),
                    new NotificationContext(owner.getId(), new AnimalTraits(saved.getAnimal().getType(), saved.getAnimal().getColor())));
        }

        if (isSearchingTransit(saved)) {
            notifyTransitHomes(saved);
        }

        log.info("Animal post created: id={} type={} owner={}", saved.getId(), saved.getType(), owner.getId());
        return AnimalPostResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalPostResponse> getAnimalPosts(GetAnimalPostsRequest request, Pageable pageable) {
        log.debug("Listing animal posts: filters={} page={}", request, pageable);
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

        post.update(request);
        animalPostRepository.save(post);
        log.info("Animal post updated: id={} by={}", animalPostId, authenticatedUser.getId());
    }

    @Override
    @Transactional
    public AnimalPostResponse transitionStatus(UUID animalPostId, TransitionStatusRequest request, User authenticatedUser) {
        AnimalPost post = getAnimalPostOrThrow(animalPostId);

        String previousStatus = post.getCurrentStatus().getStatus().name();
        post.transitionTo(request.status());
        log.info("Animal post status changed: id={} {} -> {} by={}",
                animalPostId, previousStatus, request.status(), authenticatedUser.getId());

        AnimalPost saved = animalPostRepository.save(post);
        if (isSearchingTransit(saved)) {
            notifyTransitHomes(saved);
        }
        return AnimalPostResponse.from(saved);
    }

    private static boolean isSearchingTransit(AnimalPost<?, ?> post) {
        return post instanceof AdoptionPost adoptionPost && adoptionPost.isSearchingTransit();
    }

    private void notifyTransitHomes(AnimalPost<?, ?> post) {
        notificationService.notify(
                NotificationType.IN_ADOPTION_AND_TRANSIT_PET,
                Map.of("postId", post.getId()),
                new NotificationContext(post.getOwner().getId()));
    }

    @Override
    public void delete(UUID animalPostId, User authenticatedUser) {
        AnimalPost post = getAnimalPostOrThrow(animalPostId);

        animalPostRepository.delete(post);
        log.info("Animal post deleted: id={} by={}", animalPostId, authenticatedUser.getId());
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
        return animalPostRepository.findAllByOwnerOrderByCreatedAtDesc(user)
                .stream()
                .map(AnimalPostResponse::from)
                .toList();
    }

    @Override
    public Set<UUID> getSearchingOwnerIds(AnimalType animalType, String animalColor) {
        return animalPostRepository.findOwnerIdsByAnimalTypeColorAndStatus(animalType, animalColor, StatusLostPost.SEARCHING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalPostResponse> getActiveLostPosts() {
        return animalPostRepository.findActiveLostPosts(LostPost.HAPPY_STATUSES)
                .stream()
                .map(AnimalPostResponse::from)
                .toList();
    }

    private static boolean isStreetAnimal(AnimalPost<?, ?> post) {
        return post.getType() == AnimalPostFilter.IN_STREET;
    }

    private static String describe(Location location) {
        return location.getAddress() + " " + location.getNumber() + ", " + location.getName();
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
