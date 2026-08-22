package com.nexo.manada_solidaria_backend.geolocation.services.implementations;

import com.nexo.manada_solidaria_backend.geolocation.clients.GeoapifyRestClient;
import com.nexo.manada_solidaria_backend.geolocation.clients.responses.GeoapifyResponse;
import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import com.nexo.manada_solidaria_backend.geolocation.services.interfaces.GeolocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GeolocationServiceImpl implements GeolocationService {

    private final GeoapifyRestClient geoapifyRestClient;

    @Override
    public List<GeolocationResponse> getGeolocations(String text, Integer limit, Double longitude, Double latitude) {
        try {
            GeoapifyResponse geoapifyResponse = geoapifyRestClient.getGeocodeAutocomplete(text, limit, longitude, latitude);
            return geoapifyResponse
                    .features()
                    .stream()
                    .map(feature -> buildGeolocationResponse(feature.properties()))
                    .toList();
        } catch (Exception e) {
            log.error("Error calling geoapify", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El servicio de geolocalizacion no se encuentra disponible.");
        }
    }

    @Override
    public GeolocationResponse getGeolocationReverse(double latitude, double longitude) {
        try {
            GeoapifyResponse geoapifyResponse = geoapifyRestClient.getGeocodeReverse(latitude, longitude);
            return geoapifyResponse
                    .features()
                    .stream()
                    .findFirst()
                    .map(feature -> buildGeolocationResponse(feature.properties()))
                    .orElse(null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El servicio de geolocalizacion no se encuentra disponible.");
        }
    }

    private GeolocationResponse buildGeolocationResponse(GeoapifyResponse.Property property) {
        return new GeolocationResponse(
                property.country(),
                property.state(),
                property.city(),
                property.municipality(),
                property.district(),
                property.street(),
                property.housenumber(),
                property.lon(),
                property.lat(),
                property.result_type(),
                property.formatted(),
                property.address_line1(),
                property.address_line2()
        );
    }
}
