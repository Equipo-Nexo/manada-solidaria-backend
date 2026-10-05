package com.nexo.manada_solidaria_backend.maps.controllers.responses;

import java.util.List;

public record MapResponse(
        List<MapItemResponse> lostAnimals,
        List<MapItemResponse> inStreetAnimals,
        List<MapItemResponse> vets
) {
}
