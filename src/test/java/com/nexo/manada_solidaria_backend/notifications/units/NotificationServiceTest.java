package com.nexo.manada_solidaria_backend.notifications.units;

import com.nexo.manada_solidaria_backend.common.configs.NotificationConfiguration;
import com.nexo.manada_solidaria_backend.notifications.components.notifiers.NotificationResolver;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import nl.martijndwars.webpush.PushService;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@SpringBootTest
@Import(NotificationConfiguration.class)
@ActiveProfiles("test")
@Sql(
        scripts = "/sql/data-setup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class NotificationServiceTest {

    @MockitoBean
    private NotificationDeliveryService notificationDeliveryService;
    @MockitoBean
    private NotificationRepository notificationRepository;
    @MockitoSpyBean(name = "pushNotificationServiceImpl")
    private NotificationResolver notificationResolver;
    @MockitoSpyBean
    private PushService pushService;
    @Autowired
    private NotificationService notificationService;

    @Test
    void notify_shouldNotifyAtLeastOneRecipient() {
        NotificationType type = NotificationType.PING;
        Notification notification = new Notification();

        when(notificationRepository.findByType(type)).thenReturn(Optional.of(notification));

        notificationService.notify(type);
        verify(notificationResolver, atLeastOnce())
                .notify(any(User.class), any(Notification.class));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/data-setup.sql",
                    "/sql/notifications/push-notifications.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void notify_push() throws JoseException, GeneralSecurityException, IOException, ExecutionException, InterruptedException {
        NotificationType type = NotificationType.PING;
        Notification notification = new Notification();

        when(notificationRepository.findByType(type)).thenReturn(Optional.of(notification));
        when(notificationDeliveryService.createNotificationDelivery(any(), notification, NotificationChannel.PUSH)).thenReturn(new NotificationDelivery());

        notificationService.notify(type);
        verify(pushService, atLeastOnce()).send(any());
    }
}
