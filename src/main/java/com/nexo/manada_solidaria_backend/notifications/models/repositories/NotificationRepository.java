package com.nexo.manada_solidaria_backend.notifications.models.repositories;

import com.nexo.manada_solidaria_backend.notifications.models.data.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
