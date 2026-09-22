package com.nexo.manada_solidaria_backend.notifications.controllers.interfaces;

import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/notifications")
public interface NotificationController {

    @PostMapping("/read")
    @ResponseStatus(NO_CONTENT)
    void markAllAsRead(
            @AuthenticationPrincipal User authenticatedUser
    );

    @PostMapping("/{notificationId}/read")
    @ResponseStatus(NO_CONTENT)
    void markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal User authenticatedUser
    );
}
