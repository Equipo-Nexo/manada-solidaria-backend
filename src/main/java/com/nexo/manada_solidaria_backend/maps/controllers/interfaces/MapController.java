package com.nexo.manada_solidaria_backend.maps.controllers.interfaces;

import com.nexo.manada_solidaria_backend.maps.controllers.responses.MapResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/map")
public interface MapController {

    @GetMapping
    MapResponse getMap();
}
