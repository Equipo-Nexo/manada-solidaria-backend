package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class NotificationDelivery {
    @ManyToOne
    private User recipient;
    @OneToMany(
            mappedBy = "notification",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NotificationStatusHistory> statusHistory;
    @Id
    private final UUID id = UUID.randomUUID();
}
