package com.nexo.manada_solidaria_backend.guards.controllers.implementations;

import com.nexo.manada_solidaria_backend.guards.controllers.interfaces.GuardController;
import com.nexo.manada_solidaria_backend.guards.controllers.responses.GuardStatusResponse;
import com.nexo.manada_solidaria_backend.guards.services.interfaces.GuardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class GuardControllerImpl implements GuardController {

    private final GuardService guardService;

    @Override
    public GuardStatusResponse getStatus() {
        return guardService.getStatus();
    }
}
