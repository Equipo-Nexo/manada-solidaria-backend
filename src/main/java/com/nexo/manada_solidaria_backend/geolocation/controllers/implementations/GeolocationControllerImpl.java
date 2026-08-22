package com.nexo.manada_solidaria_backend.geolocation.controllers.implementations;

import com.nexo.manada_solidaria_backend.geolocation.controllers.interfaces.GeolocationController;
import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import com.nexo.manada_solidaria_backend.geolocation.services.interfaces.GeolocationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class GeolocationControllerImpl implements GeolocationController {

    private final GeolocationService geolocationService;

    @Override
    public List<GeolocationResponse> getGeolocations(String text, Integer limit, Double longitude, Double latitude) {
        return geolocationService.getGeolocations(text, limit, longitude, latitude);
    }

    @Override
    public GeolocationResponse getGeolocationReverse(double latitude, double longitude) {
        return geolocationService.getGeolocationReverse(latitude, longitude);
    }
}
