package com.nexo.manada_solidaria_backend.animal_post.services.implementations;

import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_post.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_post.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.AdoptionPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_post.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_post.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_post.data.models.LostPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_post.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_post.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class AnimalPostServiceImpl implements AnimalPostService {

    private final AnimalPostRepository animalPostRepository;

    @Override
    @Transactional
    public AnimalPostResponse create(CreateAnimalPostRequest request, User owner) {
        Animal animal = buildAnimal(request.animal());
        Location location = buildLocation(request.location());

        AnimalPost post = switch (request.type()) {
            case LOST -> buildLostPost(request, animal, location, owner);
            case ADOPTION -> buildAdoptionPost(request, animal, location, owner);
        };

        AnimalPost saved = animalPostRepository.save(post);
        String initialStatus = switch (request.type()) {
            case LOST -> StatusLostPost.CREATED.name();
            case ADOPTION -> StatusAdoptionPost.CREATED.name();
        };
        return AnimalPostResponse.from(saved, initialStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalPostResponse> findAll(AnimalPostType type, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnimalPost> posts = type == null
                ? animalPostRepository.findAll(sorted)
                : animalPostRepository.findAllByType(toEntityClass(type), sorted);
        return posts.map(post -> AnimalPostResponse.from(post, resolveCurrentStatus(post)));
    }

    private Class<? extends AnimalPost> toEntityClass(AnimalPostType type) {
        return switch (type) {
            case LOST -> LostPost.class;
            case ADOPTION -> AdoptionPost.class;
        };
    }

    private String resolveCurrentStatus(AnimalPost post) {
        List<? extends StatusHistory<?>> history = switch (post) {
            case LostPost lost -> lost.getStatusHistory();
            case AdoptionPost adoption -> adoption.getStatusHistory();
            default -> List.of();
        };
        if (history == null) {
            return null;
        }
        return history.stream()
                .max(Comparator.comparing(StatusHistory::getCreatedAt))
                .map(entry -> entry.getStatus().name())
                .orElse(null);
    }

    private Animal buildAnimal(CreateAnimalPostRequest.AnimalRequest req) {
        Animal animal = new Animal();
        animal.setType(req.type());
        animal.setSize(req.size());
        animal.setGender(req.gender());
        animal.setColor(req.color());
        animal.setBreed(req.breed());
        animal.setFur(req.fur());
        animal.setAge(req.age());
        animal.setDescription(req.description());
        return animal;
    }

    private Location buildLocation(CreateAnimalPostRequest.LocationRequest req) {
        Location location = new Location();
        location.setName(req.name());
        location.setAddress(req.address());
        location.setNumber(req.number());
        location.setLatitude(req.latitude());
        location.setLongitude(req.longitude());
        return location;
    }

    private LostPost buildLostPost(CreateAnimalPostRequest request, Animal animal, Location location, User owner) {
        LostPost post = new LostPost(
                request.title(),
                request.description(),
                request.imageId(),
                null,
                request.hasOwner(),
                owner,
                location,
                animal
        );
        LostPostStatusHistory status = new LostPostStatusHistory(StatusLostPost.CREATED);
        status.setPost(post);
        post.setStatusHistory(new ArrayList<>(List.of(status)));
        return post;
    }

    private AdoptionPost buildAdoptionPost(CreateAnimalPostRequest request, Animal animal, Location location, User owner) {
        AdoptionPost post = new AdoptionPost(
                request.title(),
                request.description(),
                request.imageId(),
                null,
                owner,
                animal,
                location
        );
        AdoptionPostStatusHistory status = new AdoptionPostStatusHistory(StatusAdoptionPost.CREATED);
        status.setPost(post);
        post.setStatusHistory(new ArrayList<>(List.of(status)));
        return post;
    }
}
