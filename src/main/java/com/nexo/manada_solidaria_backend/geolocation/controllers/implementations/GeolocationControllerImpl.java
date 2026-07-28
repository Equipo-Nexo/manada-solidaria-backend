package com.nexo.manada_solidaria_backend.geolocation.controllers.implementations;

import com.nexo.manada_solidaria_backend.geolocation.controllers.interfaces.GeolocationController;
import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import com.nexo.manada_solidaria_backend.geolocation.services.interfaces.GeolocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class GeolocationControllerImpl implements GeolocationController {

    private final GeolocationService geolocationService;

    @Override
    public GeolocationResponse locate(HttpServletRequest request) {
        return geolocationService.locate(withoutIpv6Brackets(request.getRemoteAddr()));
    }

    private static String withoutIpv6Brackets(String ip) {
        return ip.startsWith("[") && ip.endsWith("]") ? ip.substring(1, ip.length() - 1) : ip;
    }
}
