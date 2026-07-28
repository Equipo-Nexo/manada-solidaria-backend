package com.nexo.manada_solidaria_backend.geolocation.controllers.interfaces;

import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/geolocation")
public interface GeolocationController {

    @GetMapping
    GeolocationResponse locate(HttpServletRequest request);
}
