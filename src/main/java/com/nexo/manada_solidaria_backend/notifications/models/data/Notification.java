package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@ToString
public class Notification {
    private String title;
    private String message;
    private String icon;
    private String redirectTo;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "notification_channels",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<NotificationChannel> channels = new HashSet<>();
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
