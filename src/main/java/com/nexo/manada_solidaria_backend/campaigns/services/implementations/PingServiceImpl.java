package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import org.springframework.stereotype.Service;

@Service
public class PingServiceImpl implements PingService {
    @Override
    public String ping() {
        return "pong";
    }
}
