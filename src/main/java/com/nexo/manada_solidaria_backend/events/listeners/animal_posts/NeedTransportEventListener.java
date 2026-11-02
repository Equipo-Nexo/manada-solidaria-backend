package com.nexo.manada_solidaria_backend.events.listeners.animal_posts;

import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostCreatedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType.NEW_CARRIAGE_REQUEST;
import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.POST_ID;

@Component
@Slf4j
public class NeedTransportEventListener extends BaseEventNotificationListener<AnimalPostCreatedEvent> {
    public NeedTransportEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(AnimalPostCreatedEvent event) {
        processEvent(event);
    }

    @Override
    protected boolean shouldNotify(AnimalPostCreatedEvent event) {
        return Boolean.TRUE.equals(event.needTransport());
    }

    @Override
    protected NotificationType getNotificationType() {
        return NEW_CARRIAGE_REQUEST;
    }

    @Override
    protected Map<String, Object> getParams(AnimalPostCreatedEvent event) {
        return  Map.of(POST_ID, event.postId());
    }
}
