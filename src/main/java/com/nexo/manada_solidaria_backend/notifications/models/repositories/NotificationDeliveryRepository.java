package com.nexo.manada_solidaria_backend.notifications.models.repositories;

import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationChannel;
import com.nexo.manada_solidaria_backend.notifications.models.data.NotificationDelivery;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationStatus;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {

    @EntityGraph(attributePaths = "statusHistory")
    List<NotificationDelivery> findAllByRecipientAndChannelOrderByCreatedAtDesc(User recipient, NotificationChannel channel);

    @EntityGraph(attributePaths = "statusHistory")
    Optional<NotificationDelivery> findByIdAndRecipient(UUID id, User recipient);

    @EntityGraph(attributePaths = "statusHistory")
    @Query("""
            SELECT delivery FROM NotificationDelivery delivery
            WHERE delivery.recipient = :recipient
              AND delivery.channel = :channel
              AND NOT EXISTS (
                  SELECT history FROM NotificationStatusHistory history
                  WHERE history.notification = delivery
                    AND history.status = :status
              )
            """)
    List<NotificationDelivery> findByRecipientAndChannelWithoutStatus(
            User recipient,
            NotificationChannel channel,
            NotificationStatus status
    );
}
