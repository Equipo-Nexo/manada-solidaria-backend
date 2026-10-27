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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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
        CompletableFuture<List<AnimalPostResponse>> animalPosts = getAnimalPostsAsync();
        CompletableFuture<List<VetInformationResponse>> vets = getVetsAsync();

        Map<AnimalPostFilter, List<MapItemResponse>> animalPostsByType = groupByType(animalPosts.join());
        DayOfWeek today = LocalDate.now(clock).getDayOfWeek();

        return new MapResponse(
                animalPostsByType.getOrDefault(AnimalPostFilter.LOST, List.of()),
                animalPostsByType.getOrDefault(AnimalPostFilter.IN_STREET, List.of()),
                vets.join().stream().map(vet -> MapItemResponse.from(vet, today)).toList()
        );
    }

    private CompletableFuture<List<AnimalPostResponse>> getAnimalPostsAsync() {
        return CompletableFuture.supplyAsync(animalPostService::getActiveLostPosts, mapExecutor);
    }

    private CompletableFuture<List<VetInformationResponse>> getVetsAsync() {
        return CompletableFuture.supplyAsync(vetInformationService::getAll, mapExecutor);
    }

    private Map<AnimalPostFilter, List<MapItemResponse>> groupByType(List<AnimalPostResponse> animalPosts) {
        return animalPosts.stream()
                .collect(Collectors.groupingBy(
                        AnimalPostResponse::type,
                        Collectors.mapping(this::toMapItem, Collectors.toList())
                ));
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
