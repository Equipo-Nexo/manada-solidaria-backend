package com.nexo.manada_solidaria_backend.notifications.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static com.nexo.manada_solidaria_backend.notifications.utils.MockNotificationDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.notifications.utils.MockNotificationDataUtils#";

    @Autowired
    private NotificationDeliveryRepository notificationDeliveryRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationDeliveryService notificationDeliveryService;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("GET /users/{userId}/notifications")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideNotificationFieldCases")
    @Sql(
            scripts = "/sql/notifications/user-notifications.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getNotifications(
            String testName,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        getNotificationsOf(ADMIN_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("Pedir las notificaciones de otro usuario devuelve 403")
    @Sql(
            scripts = "/sql/notifications/user-notifications.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getAnotherUsersNotificationsIsForbidden() throws Exception {
        getNotificationsOf(NOT_ADMIN_ID)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", hasItem(containsString("tus propias notificaciones"))));
    }

    @Test
    @DisplayName("Marcar una notificacion la deja leida en la base")
    @Sql(
            scripts = "/sql/notifications/user-notifications.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void markAsReadPersistsTheStatus() throws Exception {
        assertThat(isRead(UNREAD_DELIVERY_ID)).isFalse();

        mockMvc.perform(
                        post("/notifications/{notificationId}/read", UNREAD_DELIVERY_ID)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        assertThat(isRead(UNREAD_DELIVERY_ID)).isTrue();
        assertThat(isRead(OLDER_UNREAD_DELIVERY_ID)).isFalse();
    }

    @Test
    @DisplayName("Marcar una notificacion de otro usuario devuelve 404")
    @Sql(
            scripts = "/sql/notifications/user-notifications.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void markingAnotherUsersNotificationIsNotFound() throws Exception {
        mockMvc.perform(
                        post("/notifications/{notificationId}/read", OTHER_USER_DELIVERY_ID)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());

        assertThat(isRead(OTHER_USER_DELIVERY_ID)).isFalse();
    }

    @Test
    @DisplayName("Marcar todas deja la campanita sin pendientes y no toca las de otros")
    @Sql(
            scripts = "/sql/notifications/user-notifications.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void markAllAsReadPersistsTheStatus() throws Exception {
        mockMvc.perform(
                        post("/notifications/read")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        assertThat(isRead(UNREAD_DELIVERY_ID)).isTrue();
        assertThat(isRead(OLDER_UNREAD_DELIVERY_ID)).isTrue();
        assertThat(isRead(READ_DELIVERY_ID)).isTrue();
        assertThat(isRead(OTHER_USER_DELIVERY_ID)).isFalse();
        assertThat(isRead(PUSH_DELIVERY_ID)).isFalse();

        getNotificationsOf(ADMIN_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasUnreadNotification", is(false)));
    }

    @Test
    @DisplayName("Marcar todas dos veces no duplica el historial")
    @Sql(
            scripts = "/sql/notifications/user-notifications.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void markAllAsReadDoesNotDuplicateTheHistory() throws Exception {
        markAllAsRead().andExpect(status().isNoContent());
        markAllAsRead().andExpect(status().isNoContent());

        assertThat(readEntriesOf(UNREAD_DELIVERY_ID)).isEqualTo(1);
        assertThat(readEntriesOf(READ_DELIVERY_ID)).isEqualTo(1);
    }

    @Test
    @DisplayName("Al crear la entrega se guarda el texto ya resuelto, no la plantilla")
    void creatingADeliverySnapshotsTheResolvedContent() throws Exception {
        Notification template = notificationRepository.save(
                new Notification("Traslado", "{pet} necesita transporte", null, "/animal-posts/{postId}", NotificationType.NEW_CARRIAGE_REQUEST)
        );
        template.setMessage("Firulais necesita transporte");
        template.setRedirectTo("/animal-posts/99");

        notificationDeliveryService.createNotificationDelivery(
                userRepository.findByUsername("admin").orElseThrow(),
                template,
                NotificationChannel.IN_APP
        );

        getNotificationsOf(ADMIN_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications[0].description", is("Firulais necesita transporte")))
                .andExpect(jsonPath("$.notifications[0].redirectTo", is("/animal-posts/99")));
    }

    private ResultActions markAllAsRead() throws Exception {
        return mockMvc.perform(
                post("/notifications/read")
                        .header("Authorization", "Bearer " + accessToken)
        );
    }

    private long readEntriesOf(String deliveryId) {
        return notificationDeliveryRepository
                .findById(UUID.fromString(deliveryId))
                .orElseThrow()
                .getStatusHistory()
                .stream()
                .filter(history -> history.getStatus() == NotificationStatus.READ)
                .count();
    }

    private ResultActions getNotificationsOf(String userId) throws Exception {
        return mockMvc.perform(
                get("/users/{userId}/notifications", userId)
                        .header("Authorization", "Bearer " + accessToken)
        );
    }

    private boolean isRead(String deliveryId) {
        return notificationDeliveryRepository
                .findById(UUID.fromString(deliveryId))
                .orElseThrow()
                .isRead();
    }
}
