package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationAction;
import com.nexo.manada_solidaria_backend.notifications.models.data.actions.base.NotificationActionType;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Notification {
    private String title;
    private String message;
    private String icon;
    @ElementCollection
    @CollectionTable(
            name = "notification_actions",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<NotificationActionType> actions;
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private LocalDateTime createdAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();

    public Notification(String title, String message, String icon, NotificationType type) {
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.type = type;
    }
}
