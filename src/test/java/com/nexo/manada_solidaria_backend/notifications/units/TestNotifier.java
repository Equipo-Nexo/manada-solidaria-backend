package com.nexo.manada_solidaria_backend.notifications.units;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.implementations.base.Notifier;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotifierTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @Mock
    private UserService userService;

    private TestNotifier notifier;

    @BeforeEach
    void setUp() {
        notifier = spy(new TestNotifier(
                notificationRepository,
                notificationDeliveryRepository,
                userService
        ));
    }

    @Test
    void shouldSendNotificationToEveryUser() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        Notification notification = mock(Notification.class);

        when(userService.findAll())
                .thenReturn(List.of(user1, user2));

        notifier.notify(notification);

        verify(notificationRepository).save(notification);
        verify(notifier).sendNotification(user1, notification);
        verify(notifier).sendNotification(user2, notification);
    }

    private static class TestNotifier extends Notifier {

        protected TestNotifier(
                NotificationRepository notificationRepository,
                NotificationDeliveryRepository notificationDeliveryRepository,
                UserService userService
        ) {
            super(
                    notificationRepository,
                    notificationDeliveryRepository,
                    userService
            );
        }

        @Override
        public void sendNotification(User user, Notification notification) {
            // implementación vacía para el test
        }

        @Override
        public NotificationChannel getNotificationChannel() {
            return NotificationChannel.PUSH;
        }
    }
}
