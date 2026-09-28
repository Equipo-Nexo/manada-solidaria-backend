package com.nexo.manada_solidaria_backend.maps.controllers.implementations;

import com.nexo.manada_solidaria_backend.maps.controllers.interfaces.MapController;
import com.nexo.manada_solidaria_backend.maps.controllers.responses.MapResponse;
import com.nexo.manada_solidaria_backend.maps.services.interfaces.MapService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MapControllerImpl implements MapController {

    private final MapService mapService;

    @Override
    public MapResponse getMap() {
        return mapService.getMap();
    }
}
