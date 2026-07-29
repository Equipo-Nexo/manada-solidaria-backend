package com.nexo.manada_solidaria_backend.geolocation.controllers.implementations;

import com.nexo.manada_solidaria_backend.geolocation.controllers.interfaces.GeolocationController;
import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import com.nexo.manada_solidaria_backend.geolocation.services.interfaces.GeolocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@AllArgsConstructor
@Slf4j
public class GeolocationControllerImpl implements GeolocationController {

    private final GeolocationService geolocationService;

    @Override
    public GeolocationResponse locate(String forwardedFor, String realIp) {
        log.info("Geolocation headers - X-Forwarded-For: {} - X-Real-IP: {}", forwardedFor, realIp);
        return geolocationService.locate(clientIp(forwardedFor, realIp));
    }

    private static String clientIp(String forwardedFor, String realIp) {
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        throw new ResponseStatusException(BAD_REQUEST, "No se pudo determinar la IP del cliente");
    }
}
