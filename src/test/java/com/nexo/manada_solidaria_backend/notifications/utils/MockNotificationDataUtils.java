package com.nexo.manada_solidaria_backend.notifications.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class MockNotificationDataUtils {

    public static final String ADMIN_ID = "22222222-2222-2222-2222-222222222222";
    public static final String NOT_ADMIN_ID = "22222222-2222-2222-2222-222222222223";

    public static final String READ_DELIVERY_ID = "55555555-5555-5555-5555-555555555551";
    public static final String UNREAD_DELIVERY_ID = "55555555-5555-5555-5555-555555555552";
    public static final String OLDER_UNREAD_DELIVERY_ID = "55555555-5555-5555-5555-555555555553";
    public static final String PUSH_DELIVERY_ID = "55555555-5555-5555-5555-555555555554";
    public static final String OTHER_USER_DELIVERY_ID = "55555555-5555-5555-5555-555555555555";

    private static Stream<Arguments> provideNotificationFieldCases() {
        return Stream.of(
                Arguments.of(
                        "trae solo las tres IN_APP del usuario del token",
                        "$.notifications", hasSize(3)
                ),
                Arguments.of(
                        "la mas nueva viene primera",
                        "$.notifications[0].title", is("Necesitan transporte")
                ),
                Arguments.of(
                        "la mas vieja viene ultima",
                        "$.notifications[2].title", is("Mascota perdida")
                ),
                Arguments.of(
                        "description sale del message de la notificacion",
                        "$.notifications[0].description", is("Una publicacion necesita transporte")
                ),
                Arguments.of(
                        "redirectTo viaja tal cual",
                        "$.notifications[0].redirectTo", is("/animal-posts/carriage")
                ),
                Arguments.of(
                        "createdAt es el del delivery, no el de la plantilla",
                        "$.notifications[0].createdAt", containsString("2025-03-12")
                ),
                Arguments.of(
                        "el id que viaja es el del delivery",
                        "$.notifications[0].id", is(UNREAD_DELIVERY_ID)
                ),
                Arguments.of(
                        "una notificacion sin READ viene como no leida",
                        "$.notifications[0].readed", is(false)
                ),
                Arguments.of(
                        "una notificacion con READ viene como leida",
                        "$.notifications[2].readed", is(true)
                ),
                Arguments.of(
                        "hasUnreadNotification es true si queda alguna sin leer",
                        "$.hasUnreadNotification", is(true)
                )
        );
    }
}
