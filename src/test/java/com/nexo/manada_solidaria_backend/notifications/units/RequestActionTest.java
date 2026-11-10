package com.nexo.manada_solidaria_backend.notifications.units;

import com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations.RequestAction;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestActionTest {

    @Test
    void createCopyWithReplacedPlaceholders_shouldResolveEachPayloadWithoutModifyingTemplate() {
        RequestAction template = new RequestAction("Renovar", (byte) 1, "/animal-posts/{animalPostId}/repost",
                "POST", Map.of("Authorization", "OTP {token}"));

        RequestAction first = (RequestAction) template.createCopyWithReplacedPlaceholders(Map.of("animalPostId", "first", "token", "first-token"));
        RequestAction second = (RequestAction) template.createCopyWithReplacedPlaceholders(Map.of("animalPostId", "second", "token", "second-token"));

        assertEquals("/animal-posts/{animalPostId}/repost", template.getUrl());
        assertEquals(Map.of("Authorization", "OTP {token}"), template.getHeaders());
        assertEquals("/animal-posts/first/repost", first.getUrl());
        assertEquals(Map.of("Authorization", "OTP first-token"), first.getHeaders());
        assertEquals("/animal-posts/second/repost", second.getUrl());
        assertEquals(Map.of("Authorization", "OTP second-token"), second.getHeaders());
        first.getHeaders().put("Authorization", "changed");
        assertEquals("OTP {token}", template.getHeaders().get("Authorization"));
        assertEquals("OTP second-token", second.getHeaders().get("Authorization"));
    }

    @Test
    void createCopyWithReplacedPlaceholders_shouldSupportMissingHeaders() {
        RequestAction template = new RequestAction("Renovar", (byte) 1, "/repost", "POST", null);

        RequestAction copy = (RequestAction) template.createCopyWithReplacedPlaceholders(Map.of());

        assertTrue(copy.getHeaders().isEmpty());
    }
}
