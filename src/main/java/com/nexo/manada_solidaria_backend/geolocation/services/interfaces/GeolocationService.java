package com.nexo.manada_solidaria_backend.geolocation.services.interfaces;

import com.nexo.manada_solidaria_backend.geolocation.controllers.responses.GeolocationResponse;

public interface GeolocationService {
    GeolocationResponse locate(String ip);
}
