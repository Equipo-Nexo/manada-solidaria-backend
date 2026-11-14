package com.nexo.manada_solidaria_backend.events.listeners.animal_posts;

import com.nexo.manada_solidaria_backend.events.data.models.AdoptionFormCreatedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType.NEW_ADOPTION_FORM;

@Component
@Slf4j
public class AdoptionFormCreatedEventListener extends BaseEventNotificationListener<AdoptionFormCreatedEvent> {

    public AdoptionFormCreatedEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(AdoptionFormCreatedEvent event) {
        processEvent(event);
    }

    @Override
    protected boolean shouldNotify(AdoptionFormCreatedEvent event) {
        return true;
    }

    @Override
    protected NotificationType getNotificationType() {
        return NEW_ADOPTION_FORM;
    }

    @Override
    protected Map<String, Object> getParams(AdoptionFormCreatedEvent event) {
        return Map.of(
                "adoptionFormId", event.adoptionFormId(),
                "applicantName", event.applicantName()
        );
    }

    @Override
    protected NotificationContext getContext(AdoptionFormCreatedEvent event) {
        return new NotificationContext(event.postOwnerId());
    }
}