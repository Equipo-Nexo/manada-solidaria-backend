package com.nexo.manada_solidaria_backend.notifications.services.implementations;

import com.nexo.manada_solidaria_backend.notifications.controllers.responses.UserNotificationsResponse;
import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.notifications.models.repositories.NotificationDeliveryRepository;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationDeliveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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
        return changeStatus(notificationDelivery, NotificationStatus.FAILED);
    }

    @Override
    @Transactional
    public NotificationDelivery markAsSent(NotificationDelivery notificationDelivery) {
        return changeStatus(notificationDelivery, NotificationStatus.SENT);
    }

    @Override
    public UserNotificationsResponse getUserNotifications(UUID userId, User authenticatedUser) {
        validateOwner(userId, authenticatedUser);
        return UserNotificationsResponse.from(findBellNotifications(authenticatedUser));
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, User authenticatedUser) {
        NotificationDelivery delivery = this.notificationDeliveryRepository
                .findByIdAndRecipient(notificationId, authenticatedUser)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La notificación no existe"
                ));
        changeStatus(delivery, NotificationStatus.READ);
    }

    @Override
    @Transactional
    public void markAllAsRead(User authenticatedUser) {
        this.notificationDeliveryRepository
                .findByRecipientAndChannelWithoutStatus(authenticatedUser, NotificationChannel.IN_APP, NotificationStatus.READ)
                .forEach(delivery -> changeStatus(delivery, NotificationStatus.READ));
    }

    private List<NotificationDelivery> findBellNotifications(User recipient) {
        return this.notificationDeliveryRepository
                .findAllByRecipientAndChannelOrderByCreatedAtDesc(recipient, NotificationChannel.IN_APP);
    }

    private NotificationDelivery changeStatus(NotificationDelivery notificationDelivery, NotificationStatus status) {
        notificationDelivery.changeStatus(status);
        return this.notificationDeliveryRepository.save(notificationDelivery);
    }

    private void validateOwner(UUID userId, User authenticatedUser) {
        if (!userId.equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo podés acceder a tus propias notificaciones"
            );
        }
    }
}
