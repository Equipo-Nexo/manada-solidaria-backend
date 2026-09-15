package com.nexo.manada_solidaria_backend.notifications.controllers.interfaces;

import com.nexo.manada_solidaria_backend.notifications.controllers.responses.UserNotificationsResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/users/{userId}/notifications")
public interface NotificationController {

    @GetMapping
    UserNotificationsResponse getNotifications(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User authenticatedUser
    );

    @PatchMapping("/read")
    @ResponseStatus(NO_CONTENT)
    void markAllAsRead(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User authenticatedUser
    );

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(NO_CONTENT)
    void markAsRead(
            @PathVariable UUID userId,
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal User authenticatedUser
    );
}
