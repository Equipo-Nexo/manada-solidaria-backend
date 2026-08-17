package com.nexo.manada_solidaria_backend.geolocation.services.interfaces;

import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;

import java.util.List;

public interface GeolocationService {
    List<GeolocationResponse> getGeolocations(String text, Integer limit, Double longitude, Double latitude);

    GeolocationResponse getGeolocationReverse(double latitude, double longitude);
}
