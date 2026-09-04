package com.nexo.manada_solidaria_backend.guards.services.interfaces;

import com.nexo.manada_solidaria_backend.guards.controllers.responses.GuardStatusResponse;

public interface GuardService {

    GuardStatusResponse getStatus();
}
