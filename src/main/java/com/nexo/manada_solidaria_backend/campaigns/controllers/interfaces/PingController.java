package com.nexo.manada_solidaria_backend.campaigns.controllers.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/ping")
public interface PingController {
    @GetMapping
    String ping();
}
