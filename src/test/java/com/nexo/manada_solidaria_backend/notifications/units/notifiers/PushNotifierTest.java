package com.nexo.manada_solidaria_backend.notifications.units.notifiers;

import com.nexo.manada_solidaria_backend.notifications.clients.WebPushClient;
import com.nexo.manada_solidaria_backend.notifications.components.notifiers.implementations.PushNotifier;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.PushSuscriptionRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.security.Security;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PushNotifierTest {

    @MockitoBean
    private PushSuscriptionRepository pushSuscriptionRepository;

    @MockitoBean
    private WebPushClient webPushClient;

    @MockitoBean
    private NotificationDeliveryService notificationDeliveryService;

    @Autowired
    private PushNotifier pushNotifier;

    private HttpResponse response;
    private StatusLine statusLine;

    @BeforeEach
    void setUp() {
        response = mock(HttpResponse.class);
        statusLine = mock(StatusLine.class);
    }


    @BeforeAll
    static void setupBouncyCastle() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @DisplayName("Should send push notification successfully - mark as sent")
    void send_push() throws Exception {
        User user = new User();
        Notification notification = new Notification();
        List<PushSubscription> userSubscriptions = List.of(new PushSubscription());

        when(pushSuscriptionRepository.findAllByUser(user)).thenReturn(userSubscriptions);
        when(notificationDeliveryService.createNotificationDelivery(user, notification, NotificationChannel.PUSH)).thenReturn(null);
        when(webPushClient.send(any(), any())).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(201);

        pushNotifier.notify(user, notification);

        verify(notificationDeliveryService, times(1)).markAsSent(any());
    }


    @Test
    @DisplayName("Should mark notification as failed when sending push notification fails")
    void sendPushWithError() throws Exception {
        User user = new User();
        Notification notification = new Notification();
        List<PushSubscription> userSubscriptions = List.of(new PushSubscription());

        when(pushSuscriptionRepository.findAllByUser(user)).thenReturn(userSubscriptions);
        when(notificationDeliveryService.createNotificationDelivery(user, notification, NotificationChannel.PUSH)).thenReturn(null);
        when(webPushClient.send(any(), any())).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(404);

        pushNotifier.notify(user, notification);

        verify(notificationDeliveryService, times(1)).markAsFailed(any());
    }
}