package com.nexo.manada_solidaria_backend.password_recovery.components;

import com.nexo.manada_solidaria_backend.password_recovery.components.PasswordRecoveryMailListener.PasswordRecoveryRequested;
import com.nexo.manada_solidaria_backend.password_recovery.config.PasswordRecoveryProperties;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.password_recovery.data.repositories.PasswordRecoveryRepository;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.nexo.manada_solidaria_backend.password_recovery.data.enums.PasswordRecoveryStatus.OPEN;

@Component
@AllArgsConstructor
@Slf4j
public class PasswordRecoveryStarter {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRecoveryProperties properties;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void start(User user) {
        Optional<PasswordRecovery> open = passwordRecoveryRepository.findByUserAndStatusIn(user, OPEN);
        if (open.filter(this::isWithinResendCooldown).isPresent()) {
            log.info("Password recovery request ignored, resend cooldown is active: user={}", user.getId());
            return;
        }

        open.ifPresent(this::revokeAndFlush);
        String code = generateCode();
        passwordRecoveryRepository.save(new PasswordRecovery(
                user,
                passwordEncoder.encode(code),
                LocalDateTime.now().plusMinutes(properties.codeExpiration())
        ));
        applicationEventPublisher.publishEvent(
                new PasswordRecoveryRequested(user.getProfile().getEmail(), code)
        );
    }

    private void revokeAndFlush(PasswordRecovery recovery) {
        recovery.revoke();
        passwordRecoveryRepository.flush();
    }

    private boolean isWithinResendCooldown(PasswordRecovery recovery) {
        return recovery.getCreatedAt()
                .isAfter(LocalDateTime.now().minusSeconds(properties.resendCooldown()));
    }

    private static String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
