package com.nexo.manada_solidaria_backend.events.listeners.animal_posts;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostStatusChangedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class AnimalRequiresTransitEventListener extends BaseEventNotificationListener<AnimalPostStatusChangedEvent> {

    public AnimalRequiresTransitEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    protected boolean shouldNotify(AnimalPostStatusChangedEvent event) {
        return animalPostIsAdoption(event) && animalRequiresTransit(event);
    }

    @Override
    protected NotificationType getNotificationType() {
        return null;
    }

    @Override
    protected Map<String, Object> getParams(AnimalPostStatusChangedEvent event) {
        return Map.of();
    }

    private static boolean animalRequiresTransit(AnimalPostStatusChangedEvent event) {
        return StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT.name().equals(event.newStatus());
    }

    private static boolean animalPostIsAdoption(AnimalPostStatusChangedEvent event) {
        return event.postType() == AnimalPostFilter.ADOPTION;
    }
}
