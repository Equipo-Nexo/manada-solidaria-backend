package com.nexo.manada_solidaria_backend.geolocation.controllers.interfaces;

import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/geolocation")
public interface GeolocationController {

    @GetMapping
    GeolocationResponse locate(
            @RequestHeader(name = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(name = "X-Real-IP", required = false) String realIp
    );
}
