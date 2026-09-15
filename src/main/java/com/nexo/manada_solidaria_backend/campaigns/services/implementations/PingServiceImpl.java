package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {

    private final NotificationService notificationService;

    @Override
    public String ping() {
        notificationService.notify(NotificationType.PING);
        return "pong";
    }
}
