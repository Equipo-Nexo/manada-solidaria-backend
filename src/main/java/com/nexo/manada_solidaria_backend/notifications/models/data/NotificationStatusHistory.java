package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class NotificationStatusHistory extends StatusHistory<NotificationStatus> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_delivery_id", nullable = false)
    private NotificationDelivery notification;

    public NotificationStatusHistory(NotificationStatus status, NotificationDelivery notification) {
        super(status);
        this.notification = notification;
    }
}
