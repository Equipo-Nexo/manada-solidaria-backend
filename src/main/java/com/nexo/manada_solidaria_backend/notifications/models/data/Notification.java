package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Notification {
    private String title;
    private String message;
    @Enumerated
    private NotificationType type;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();
}
