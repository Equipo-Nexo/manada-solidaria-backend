package com.nexo.manada_solidaria_backend.notifications.utils;

import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationSubscriptionRequest;
import com.nexo.manada_solidaria_backend.notifications.controllers.requests.PushNotificationUnsuscribeRequest;

public class MockNotificationsDataUtils {

    public static final String MOCK_ENDPOINT = "https://example.com/push-service/send";
    public static final PushNotificationSubscriptionRequest SUBSCRIPTION_REQUEST_MOCK = new PushNotificationSubscriptionRequest(
            MOCK_ENDPOINT,
            null,
            new PushNotificationSubscriptionRequest.Key(
                    "mockP256dhKey",
                    "mockAuthKey"
            )
    );
    public static final PushNotificationUnsuscribeRequest UNSUBSCRIBE_REQUEST_MOCK = new PushNotificationUnsuscribeRequest(
            MOCK_ENDPOINT
    );
}
