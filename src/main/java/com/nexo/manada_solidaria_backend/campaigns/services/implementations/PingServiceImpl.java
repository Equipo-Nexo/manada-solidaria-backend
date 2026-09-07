package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.base.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {

    private final NotificationService notificationService;

    @Override
    public String ping() {
        notificationService.notify(new Notification(
                "Ping",
                "Pong",
                null,
                null,
                NotificationType.NEW_DONATION_CAMPAIGN
        ));
        return "pong";
    }
}
