package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Notification {
    private String title;
    private String message;
    private String icon;
    private String redirectTo;
    @Enumerated
    private NotificationType type;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();

    public Notification(String title, String message, String icon, String redirectTo, NotificationType type) {
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.redirectTo = redirectTo;
        this.type = type;
    }
}
