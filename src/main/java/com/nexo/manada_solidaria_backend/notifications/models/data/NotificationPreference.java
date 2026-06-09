package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@Entity
public class NotificationPreference {
    @ManyToOne
    private final Profile profile;
    private NotificationType type;
    @Id
    private final UUID id = UUID.randomUUID();

    public NotificationPreference(Profile profile) {
        this.profile = profile;
    }
}
