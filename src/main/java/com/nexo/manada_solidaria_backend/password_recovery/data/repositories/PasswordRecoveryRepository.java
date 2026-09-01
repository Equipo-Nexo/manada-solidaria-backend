package com.nexo.manada_solidaria_backend.password_recovery.data.repositories;

import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, UUID> {

    List<PasswordRecovery> findByUserAndUsedAtIsNull(User user);

    Optional<PasswordRecovery> findFirstByUserAndUsedAtIsNullOrderByCreatedAtDesc(User user);

    Optional<PasswordRecovery> findByResetTokenHash(String resetTokenHash);
}
