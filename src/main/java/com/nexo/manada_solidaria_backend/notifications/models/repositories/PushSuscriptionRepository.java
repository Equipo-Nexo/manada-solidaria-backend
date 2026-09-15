package com.nexo.manada_solidaria_backend.notifications.models.repositories;

import com.nexo.manada_solidaria_backend.notifications.models.data.PushSubscription;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushSuscriptionRepository extends JpaRepository<PushSubscription, UUID> {
    Optional<PushSubscription> findByEndpoint(String endpoint);

    void deleteByEndpointAndUser(@NotBlank String endpoint, User user);

    List<PushSubscription> findAllByUser(User user);
}
