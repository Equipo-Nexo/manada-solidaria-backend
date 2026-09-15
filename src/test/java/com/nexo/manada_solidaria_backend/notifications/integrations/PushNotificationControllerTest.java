package com.nexo.manada_solidaria_backend.notifications.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.nexo.manada_solidaria_backend.notifications.utils.MockNotificationsDataUtils.SUBSCRIPTION_REQUEST_MOCK;
import static com.nexo.manada_solidaria_backend.notifications.utils.MockNotificationsDataUtils.UNSUBSCRIBE_REQUEST_MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PushNotificationControllerTest extends BaseAuthenticatedIntegrationTest {

    @DisplayName("Push Notification Controller - Test suscribe endpoint")
    @Test
    void testSubscribeEndpoint() throws Exception {

        mockMvc
                .perform(
                        post("/push-notifications/subscribe")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType("application/json")
                                .content(toJson(SUBSCRIPTION_REQUEST_MOCK)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Push Notification Controller - Test unsubscribe endpoint")
    @Test
    @Sql(
            scripts = {
                    "/sql/notifications/push-notifications.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void testUnsubscribeEndpoint() throws Exception {
        mockMvc
                .perform(
                        delete("/push-notifications/unsubscribe")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType("application/json")
                                .content(toJson(UNSUBSCRIBE_REQUEST_MOCK)))
                .andExpect(status().isNoContent());
    }
}
