package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private final NotificationDeliveryRepository notificationDeliveryRepository;

    @Override
    @Transactional
    public NotificationDelivery createNotificationDelivery(User user, Notification notification, NotificationChannel channel) {
        return this.notificationDeliveryRepository.save(new NotificationDelivery(
                user,
                notification,
                channel,
                NotificationStatus.PENDING
        ));
    }

    @Override
    @Transactional
    public NotificationDelivery markAsFailed(NotificationDelivery notificationDelivery) {
        notificationDelivery.changeStatus(NotificationStatus.FAILED);
        return this.notificationDeliveryRepository.save(notificationDelivery);
    }

    @Override
    @Transactional
    public NotificationDelivery markAsSent(NotificationDelivery notificationDelivery) {
        notificationDelivery.changeStatus(NotificationStatus.SENT);
        return this.notificationDeliveryRepository.save(notificationDelivery);
    }
}
