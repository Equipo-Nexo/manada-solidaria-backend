package com.nexo.manada_solidaria_backend.maps.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.maps.controllers.responses.MapItemResponse;
import com.nexo.manada_solidaria_backend.maps.controllers.responses.MapResponse;
import com.nexo.manada_solidaria_backend.maps.services.interfaces.MapService;
import com.nexo.manada_solidaria_backend.vets.controllers.responses.VetInformationResponse;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MapServiceImpl implements MapService {

    private final AnimalPostService animalPostService;
    private final VetInformationService vetInformationService;
    private final Clock clock;
    private final Executor mapExecutor;

    @Override
    public MapResponse getMap() {
        CompletableFuture<List<AnimalPostResponse>> lostPosts =
                CompletableFuture.supplyAsync(animalPostService::getActiveLostPosts, mapExecutor);
        CompletableFuture<List<VetInformationResponse>> vets =
                CompletableFuture.supplyAsync(() -> vetInformationService.getAll(null, false, null, null), mapExecutor);
        CompletableFuture<List<VetInformationResponse>> openVets =
                CompletableFuture.supplyAsync(() -> vetInformationService.getAll(null, true, null, null), mapExecutor);

        Map<AnimalPostFilter, List<MapItemResponse>> animalsByType = lostPosts.join().stream()
                .collect(Collectors.groupingBy(AnimalPostResponse::type, Collectors.mapping(this::toMapItem, Collectors.toList())));
        Set<UUID> openVetIds = openVets.join().stream()
                .map(VetInformationResponse::id)
                .collect(Collectors.toSet());

        return new MapResponse(
                animalsByType.getOrDefault(AnimalPostFilter.LOST, List.of()),
                animalsByType.getOrDefault(AnimalPostFilter.IN_STREET, List.of()),
                vets.join().stream()
                        .map(vet -> MapItemResponse.from(vet, openVetIds.contains(vet.id())))
                        .toList()
        );
    }

    private MapItemResponse toMapItem(AnimalPostResponse post) {
        return MapItemResponse.from(post, daysSincePublished(post.createdAt()));
    }

    private long daysSincePublished(LocalDateTime createdAt) {
        LocalDate publishedOn = createdAt.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(clock.getZone())
                .toLocalDate();
        return ChronoUnit.DAYS.between(publishedOn, LocalDate.now(clock));
    }
}
