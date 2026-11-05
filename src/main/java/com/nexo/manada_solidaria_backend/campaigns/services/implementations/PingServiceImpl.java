package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.PingService;
import com.nexo.manada_solidaria_backend.events.EventPublisher;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.password_recovery.services.interfaces.PasswordRecoveryService;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {

    private final EventPublisher eventPublisher;
    private final UserService userService;
    private final PasswordRecoveryService passwordRecoveryService;

    @Override
    @Transactional
    public String ping() {
        eventPublisher.publishPingEvent();
        PasswordRecovery verificationCode = passwordRecoveryService.createTotp(
                userService.getUserById(UUID.fromString("be71da91-062b-47ff-b363-68ed594481df"))
        );
        log.info("PasswordRecovery {}", verificationCode.getVerificationCode());
        return "pong";
    }
}
