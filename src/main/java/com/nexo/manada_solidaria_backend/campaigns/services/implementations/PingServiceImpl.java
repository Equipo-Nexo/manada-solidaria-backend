package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import com.nexo.manada_solidaria_backend.events.EventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {

    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public String ping() {
        eventPublisher.publishPingEvent();
        return "pong";
    }
}
