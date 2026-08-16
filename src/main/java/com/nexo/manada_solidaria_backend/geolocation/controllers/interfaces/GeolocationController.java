package com.nexo.manada_solidaria_backend.geolocation.controllers.interfaces;

import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/geolocation")
public interface GeolocationController {

    @GetMapping
    List<GeolocationResponse> getGeolocations(
            @RequestParam String text,
            @RequestParam(defaultValue = "5") int limit
    );

    @GetMapping("/reverse")
    GeolocationResponse getGeolocationReverse(
            @RequestParam double latitude,
            @RequestParam double longitude
    );
}
