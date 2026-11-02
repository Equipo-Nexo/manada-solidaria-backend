package com.nexo.manada_solidaria_backend.events.listeners.animal_posts;

import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostCreatedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter.IN_STREET;
import static com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType.SIMILAR_ANIMAL_RECENTLY_LOST;
import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.LOCATION;
import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.POST_ID;


@Component
@Slf4j
public class InStreetAnimalEventListener extends BaseEventNotificationListener<AnimalPostCreatedEvent> {

    public InStreetAnimalEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    protected boolean shouldNotify(AnimalPostCreatedEvent event) {
        return event.type() == IN_STREET;
    }

    @Override
    protected NotificationType getNotificationType() {
        return SIMILAR_ANIMAL_RECENTLY_LOST;
    }

    @Override
    protected Map<String, Object> getParams(AnimalPostCreatedEvent event) {
        return Map.of(
                POST_ID, event.postId(),
                LOCATION, describe(event.location())
        );
    }


    private String describe(Location location) {
        return location.getAddress() + " " + location.getNumber() + ", " + location.getName();
    }
}
