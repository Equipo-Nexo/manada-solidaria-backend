package com.nexo.manada_solidaria_backend.events.listeners.animal_posts;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostCreatedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType.IN_ADOPTION_AND_TRANSIT_PET;
import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.POST_ID;

@Component
@Slf4j
public class IsSearchingTransitEventListener extends BaseEventNotificationListener<AnimalPostCreatedEvent> {

    public IsSearchingTransitEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    protected boolean shouldNotify(AnimalPostCreatedEvent event) {
        return isAdoptionPost(event) && statusIsSearchinngAdoptAndTransit(event);
    }

    @Override
    protected NotificationType getNotificationType() {
        return IN_ADOPTION_AND_TRANSIT_PET;
    }

    @Override
    protected Map<String, Object> getParams(AnimalPostCreatedEvent event) {
        return Map.of(POST_ID, event.postId());
    }

    @Override
    protected NotificationContext getContext(AnimalPostCreatedEvent event) {
        return new NotificationContext(event.ownerId());
    }


    private boolean statusIsSearchinngAdoptAndTransit(AnimalPostCreatedEvent event) {
        return StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT.name().equals(event.status());
    }

    private static boolean isAdoptionPost(AnimalPostCreatedEvent event) {
        return event.type() == AnimalPostFilter.ADOPTION;
    }
}
