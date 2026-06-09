package com.nexo.manada_solidaria_backend.campaigns.controllers.implementations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.interfaces.PingController;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PingControllerImpl implements PingController {

    private final PingService pingService;

    @Override
    public String ping() {
        return pingService.ping();
    }
}
