package com.nexo.manada_solidaria_backend.geolocation.services.implementations;

import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import com.nexo.manada_solidaria_backend.geolocation.services.interfaces.GeolocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class GeolocationServiceImpl implements GeolocationService {

    private static final String NOT_FOUND_MESSAGE = "No se pudo geolocalizar la IP";
    private static final String UNAVAILABLE_MESSAGE = "El servicio de geolocalizacion no esta disponible";

    private final RestClient geoIpRestClient;

    @Override
    public GeolocationResponse locate(String ip) {
        GeoIpApiResponse data;
        try {
            data = geoIpRestClient.get()
                    .uri("/{ip}", ip)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        log.warn("GeoIP no resolvio la IP {} - status {}", ip, response.getStatusCode());
                        throw new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        log.error("GeoIP respondio con error para la IP {} - status {}", ip, response.getStatusCode());
                        throw new ResponseStatusException(BAD_GATEWAY, UNAVAILABLE_MESSAGE);
                    })
                    .body(GeoIpApiResponse.class);
        } catch (RestClientException e) {
            log.error("Fallo la conexion con GeoIP para la IP {}", ip, e);
            throw new ResponseStatusException(BAD_GATEWAY, UNAVAILABLE_MESSAGE);
        }

        if (data == null || data.latitude() == null || data.longitude() == null) {
            log.warn("GeoIP respondio sin coordenadas para la IP {}", ip);
            throw new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE);
        }

        log.info("GeoIP resolvio la IP {} - {}, {}", ip, data.city(), data.country());

        return new GeolocationResponse(
                data.latitude(),
                data.longitude(),
                data.city(),
                data.stateprov(),
                data.country()
        );
    }
}
