package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class NotificationDelivery {
    @ManyToOne(optional = false)
    private User recipient;
    @ManyToOne(optional = false)
    private Notification notification;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;
    @OneToMany(
            mappedBy = "notification",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NotificationStatusHistory> statusHistory = new ArrayList<>();
    @Id
    private final UUID id = UUID.randomUUID();

    public NotificationDelivery(User recipient, Notification notification, NotificationChannel channel, NotificationStatus status) {
        this.recipient = recipient;
        this.notification = notification;
        this.channel = channel;
        this.statusHistory.add(new NotificationStatusHistory(status, this));
    }
}
