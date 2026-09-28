package com.nexo.manada_solidaria_backend.maps.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.maps.controllers.responses.MapResponse;
import com.nexo.manada_solidaria_backend.maps.services.interfaces.MapService;
import com.nexo.manada_solidaria_backend.vets.services.interfaces.VetInformationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MapServiceImpl implements MapService {

    private final AnimalPostService animalPostService;
    private final VetInformationService vetInformationService;

    @Override
    public MapResponse getMap() {
        return new MapResponse(
                animalPostService.getLostMapItems(),
                animalPostService.getInStreetMapItems(),
                vetInformationService.getMapItems()
        );
    }
}
