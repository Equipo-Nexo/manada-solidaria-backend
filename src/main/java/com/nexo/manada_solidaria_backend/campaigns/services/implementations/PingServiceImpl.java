package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import com.nexo.manada_solidaria_backend.events.data.models.PingEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String ping() {
        eventPublisher.publishEvent(new PingEvent());
        return "pong";
    }
}
