package com.nexo.manada_solidaria_backend.notifications.units;

import com.nexo.manada_solidaria_backend.notifications.models.data.actions.implementations.RequestAction;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestActionTest {

    @Test
    void copy_shouldResolveEachPayloadWithoutModifyingTemplate() {
        RequestAction template = new RequestAction("Renovar", "/animal-posts/{animalPostId}/repost",
                "POST", Map.of("Authorization", "OTP {token}"));

        RequestAction first = (RequestAction) template.copy(Map.of("animalPostId", "first", "token", "first-token"));
        RequestAction second = (RequestAction) template.copy(Map.of("animalPostId", "second", "token", "second-token"));

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
    void copy_shouldSupportMissingHeaders() {
        RequestAction template = new RequestAction("Renovar", "/repost", "POST", null);

        RequestAction copy = (RequestAction) template.copy(Map.of());

        assertTrue(copy.getHeaders().isEmpty());
    }
}
