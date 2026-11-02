package com.nexo.manada_solidaria_backend.events.listeners.ping;

import com.nexo.manada_solidaria_backend.events.data.models.PingEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType.PING;

@Component
@Slf4j
public class PingEventListener extends BaseEventNotificationListener<PingEvent> {
    public PingEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    protected boolean shouldNotify(PingEvent event) {
        return true;
    }

    @Override
    protected NotificationType getNotificationType() {
        return PING;
    }

    @Override
    protected Map<String, Object> getParams(PingEvent event) {
        return Map.of();
    }
}
