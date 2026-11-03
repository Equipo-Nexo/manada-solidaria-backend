package com.nexo.manada_solidaria_backend.events.listeners;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostCreatedEvent;
import com.nexo.manada_solidaria_backend.events.data.models.AnimalPostStatusChangedEvent;
import com.nexo.manada_solidaria_backend.events.data.models.NewCampaignCreatedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.animal_posts.AnimalRequiresTransitEventListener;
import com.nexo.manada_solidaria_backend.events.listeners.animal_posts.InStreetAnimalEventListener;
import com.nexo.manada_solidaria_backend.events.listeners.animal_posts.NeedTransportEventListener;
import com.nexo.manada_solidaria_backend.events.listeners.campaigns.NewFundraisingCampaignEventListener;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost.SEARCHING;
import static com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost.TO_RESCUE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenersTest {
    @Mock
    private NotificationService notificationService;

    private final UUID postId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final NotificationContext context = new NotificationContext(ownerId);
    private final Location location = new Location("Argentina", "Villa María", "Esquina", "Corrientes 500", "Corrientes", 500, -34.6, -58.4);

    @ParameterizedTest
    @EnumSource(AnimalPostFilter.class)
    void streetNotification_onlyForStreetAnimals(AnimalPostFilter type) {
        new InStreetAnimalEventListener(notificationService).onEvent(created(type, false, TO_RESCUE));
        if (type == AnimalPostFilter.IN_STREET) {
            verify(notificationService).notify(NotificationType.SIMILAR_ANIMAL_RECENTLY_LOST,
                    Map.of("postId", postId, "location", "Corrientes 500, Villa María"), context);
            verifyNoMoreInteractions(notificationService);
        } else {
            verifyNoInteractions(notificationService);
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(booleans = {true, false})
    void transportNotification_onlyWhenRequested(Boolean transport) {
        new NeedTransportEventListener(notificationService)
                .onEvent(created(AnimalPostFilter.LOST, transport, SEARCHING));
        if (Boolean.TRUE.equals(transport)) {
            verify(notificationService).notify(NotificationType.NEW_CARRIAGE_REQUEST,
                    Map.of("postId", postId), null);
            verifyNoMoreInteractions(notificationService);
        } else {
            verifyNoInteractions(notificationService);
        }
    }

    static Stream<Arguments> transitCases() {
        return Arrays.stream(AnimalPostFilter.values()).flatMap(type ->
                Arrays.stream(StatusAdoptionPost.values()).flatMap(status ->
                        Stream.of(Arguments.of(type, status, true), Arguments.of(type, status, false))));
    }

    @ParameterizedTest
    @MethodSource("transitCases")
    void transitNotification_forCreationAndStatusChanges(AnimalPostFilter type, StatusAdoptionPost status,
                                                         boolean creation) {
        var listener = new AnimalRequiresTransitEventListener(notificationService);
        if (creation) {
            listener.onEvent(created(type, false, status));
        } else {
            listener.onEvent(new AnimalPostStatusChangedEvent(postId, ownerId,
                    "SEARCHING_ADOPT", status, type));
        }
        if (type == AnimalPostFilter.ADOPTION && status == StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT) {
            verify(notificationService).notify(NotificationType.IN_ADOPTION_AND_TRANSIT_PET,
                    Map.of("postId", postId), context);
            verifyNoMoreInteractions(notificationService);
        } else {
            verifyNoInteractions(notificationService);
        }
    }

    @ParameterizedTest
    @EnumSource(CampaignType.class)
    void campaignNotification_onlyForFundraising(CampaignType type) {
        new NewFundraisingCampaignEventListener(notificationService)
                .onEvent(new NewCampaignCreatedEvent(type, "autor", postId, ownerId));
        if (type == CampaignType.FUNDRAISING) {
            verify(notificationService).notify(NotificationType.NEW_FUNDRAISING_CAMPAIGN,
                    Map.of("user", "autor", "fundraisingId", postId), context);
            verifyNoMoreInteractions(notificationService);
        } else {
            verifyNoInteractions(notificationService);
        }
    }

    private AnimalPostCreatedEvent created(AnimalPostFilter type, Boolean transport, Enum<?> status) {
        return new AnimalPostCreatedEvent(postId, ownerId, transport, type, location, status, context);
    }
}
