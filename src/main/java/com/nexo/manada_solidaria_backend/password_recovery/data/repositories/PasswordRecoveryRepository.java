package com.nexo.manada_solidaria_backend.password_recovery.data.repositories;

import com.nexo.manada_solidaria_backend.password_recovery.data.enums.PasswordRecoveryStatus;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, UUID> {

    Optional<PasswordRecovery> findByUserAndStatusIn(User user, Collection<PasswordRecoveryStatus> statuses);

    Optional<PasswordRecovery> findByUserProfileEmailAndStatus(String email, PasswordRecoveryStatus status);

    Optional<PasswordRecovery> findByResetToken(String resetToken);
}
