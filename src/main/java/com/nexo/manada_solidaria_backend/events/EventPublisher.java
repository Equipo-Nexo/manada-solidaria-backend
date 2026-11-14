package com.nexo.manada_solidaria_backend.events;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.events.data.models.AdoptionFormCreatedEvent;
import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostCreatedEvent;
import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostStatusChangedEvent;
import com.nexo.manada_solidaria_backend.events.data.models.NewCampaignCreatedEvent;
import com.nexo.manada_solidaria_backend.events.data.models.PingEvent;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishNewAnimalPostEvent(CreateAnimalPostRequest request, User owner, AnimalPost saved) {
        publishEvent(
                new AnimalPostCreatedEvent(
                    saved.getId(),
                    owner.getId(),
                    request.needTransport(),
                    saved.getType(),
                    saved.getLocation(),
                    saved.getCurrentStatus().getStatus(),
                    new NotificationContext(owner.getId(), new NotificationContext.AnimalTraits(saved.getAnimal().getType(), saved.getAnimal().getColor()))
                )
        );
    }

    public void publishAnimalPostStatusChangedEvent(User authenticatedUser, AnimalPost saved, String previousStatus) {
        publishEvent(
                new AnimalPostStatusChangedEvent(
                        saved.getId(),
                        authenticatedUser.getId(),
                        previousStatus,
                        saved.getCurrentStatus().getStatus(),
                        saved.getType()
                )
        );
    }

    public void publishNewCampaignEvent(User owner, Campaign<?, ?> saved) {
        publishEvent(
                new NewCampaignCreatedEvent(
                        saved.getCampaignType(),
                        owner.getUsername(),
                        saved.getId(),
                        owner.getId()
                )
        );
    }

    public void publishAdoptionFormCreatedEvent(UUID adoptionFormId, UUID adoptionPostId, UUID postOwnerId, String applicantName) {
        publishEvent(
                new AdoptionFormCreatedEvent(
                        adoptionFormId,
                        adoptionPostId,
                        postOwnerId,
                        applicantName
                )
        );
    }

    public void publishPingEvent() {
        publishEvent(new PingEvent());
    }

    private void publishEvent(Object event) {
        eventPublisher.publishEvent(event);
    }

}