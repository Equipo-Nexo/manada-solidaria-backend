package com.nexo.manada_solidaria_backend.notifications.units;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.implementations.PushNotificationServiceImpl;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushNotificationUnitTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @Mock
    private PushService pushService;

    @Mock
    private PushSuscriptionRepository pushSuscriptionRepository;

    @Mock
    private UserService userService;

    private PushNotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PushNotificationServiceImpl(
                notificationRepository,
                notificationDeliveryRepository,
                pushService,
                pushSuscriptionRepository,
                new ObjectMapper(),
                userService
        );
    }

    @Test
    void shouldSendPushNotificationAndRecordDeliveryAsSent() throws Exception {
        User user = mock(User.class);
        PushSubscription subscription = new PushSubscription();
        subscription.setEndpoint("https://push.example.com/123");
        subscription.setP256dh("p256dh");
        subscription.setAuth("auth");

        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(pushSuscriptionRepository.findAllByUser(user)).thenReturn(List.of(subscription));
        when(pushService.send(any(Notification.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(201);

        try (MockedConstruction<Notification> mocked = Mockito.mockConstruction(Notification.class)) {
            var notification = new com.nexo.manada_solidaria_backend.notifications.models.data.Notification();

            service.sendNotification(user, notification);

            verify(pushService).send(any(Notification.class));
            verify(notificationDeliveryRepository).save(
                    argThat(delivery ->
                            delivery.getRecipient().equals(user)
                                    && delivery.getNotification().equals(notification)
                                    && delivery.getChannel() == NotificationChannel.PUSH
                    )
            );
        }
    }

    @Test
    void shouldRecordDeliveryAsFailedWhenPushProviderReturnsError() throws Exception {
        User user = mock(User.class);
        PushSubscription subscription = new PushSubscription();
        subscription.setEndpoint("https://push.example.com/123");
        subscription.setP256dh("p256dh");
        subscription.setAuth("auth");
        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(pushSuscriptionRepository.findAllByUser(user)).thenReturn(List.of(subscription));
        when(pushService.send(any(nl.martijndwars.webpush.Notification.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(410);
        when(statusLine.getReasonPhrase()).thenReturn("Gone");


        try (MockedConstruction<Notification> mocked = Mockito.mockConstruction(Notification.class)) {
            var notification = new com.nexo.manada_solidaria_backend.notifications.models.data.Notification();
            service.sendNotification(user, notification);

            verify(notificationDeliveryRepository).save(
                    argThat(delivery ->
                            delivery.getChannel() == NotificationChannel.PUSH
                    )
            );
        }
    }
}
