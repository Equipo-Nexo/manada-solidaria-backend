package com.nexo.manada_solidaria_backend.common.controllers.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import com.nexo.manada_solidaria_backend.common.controllers.interfaces.PingController;
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
