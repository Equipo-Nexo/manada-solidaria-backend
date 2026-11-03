package com.nexo.manada_solidaria_backend.events.listeners.animal_posts;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.events.data.models.BaseAnimalPostStatusChanged;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.POST_ID;

@Component
@Slf4j
public class AnimalRequiresTransitEventListener extends BaseEventNotificationListener<BaseAnimalPostStatusChanged> {

    public AnimalRequiresTransitEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(BaseAnimalPostStatusChanged event) {
        processEvent(event);
    }

    @Override
    protected boolean shouldNotify(BaseAnimalPostStatusChanged event) {
        return animalPostIsAdoption(event.type()) && animalRequiresTransit(event.status());
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.IN_ADOPTION_AND_TRANSIT_PET;
    }

    @Override
    protected Map<String, Object> getParams(BaseAnimalPostStatusChanged event) {
        return Map.of(POST_ID, event.postId());
    }

    @Override
    protected NotificationContext getContext(BaseAnimalPostStatusChanged event) {
        return new NotificationContext(event.ownerId());
    }

    private static boolean animalRequiresTransit(Enum<?> status) {
        return status == StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT;
    }

    private static boolean animalPostIsAdoption(AnimalPostFilter type) {
        return type == AnimalPostFilter.ADOPTION;
    }
}
