package com.nexo.manada_solidaria_backend.password_recovery.services.implementations;

import com.nexo.manada_solidaria_backend.password_recovery.components.PasswordRecoveryMailer;
import com.nexo.manada_solidaria_backend.password_recovery.config.PasswordRecoveryProperties;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.responses.RecoveryTokenResponse;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.password_recovery.data.repositories.PasswordRecoveryRepository;
import com.nexo.manada_solidaria_backend.password_recovery.services.interfaces.PasswordRecoveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import static com.nexo.manada_solidaria_backend.password_recovery.data.enums.PasswordRecoveryStatus.ACTIVE;
import static com.nexo.manada_solidaria_backend.password_recovery.data.enums.PasswordRecoveryStatus.OPEN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private static final String INVALID_CODE_MESSAGE = "El código de recuperación no es válido";
    private static final String INVALID_TOKEN_MESSAGE = "El token de recuperación no es válido";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final StringKeyGenerator TOKEN_GENERATOR =
            new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 32);

    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRecoveryMailer passwordRecoveryMailer;
    private final PasswordRecoveryProperties properties;

    @Override
    @Transactional
    public void requestRecovery(RequestRecoveryRequest request) {
        userService.findByEmail(request.email()).ifPresent(this::startRecovery);
    }

    @Override
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public RecoveryTokenResponse verifyCode(VerifyRecoveryCodeRequest request) {
        PasswordRecovery recovery = passwordRecoveryRepository
                .findByUserProfileEmailAndStatus(request.email(), ACTIVE)
                .filter(this::isCodeUsable)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, INVALID_CODE_MESSAGE));

        if (!passwordEncoder.matches(request.code(), recovery.getVerificationCode())) {
            recovery.registerFailedAttempt();
            log.info("Invalid password recovery code: user={}", recovery.getUser().getId());
            throw new ResponseStatusException(BAD_REQUEST, INVALID_CODE_MESSAGE);
        }

        String resetToken = TOKEN_GENERATOR.generateKey();
        recovery.markVerified(
                hash(resetToken),
                LocalDateTime.now().plusMinutes(properties.getTokenExpiration())
        );
        log.info("Password recovery code verified: user={}", recovery.getUser().getId());
        return new RecoveryTokenResponse(resetToken);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordRecovery recovery = passwordRecoveryRepository.findByResetToken(hash(request.resetToken()))
                .filter(this::isResetTokenUsable)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, INVALID_TOKEN_MESSAGE));

        userService.updatePassword(recovery.getUser(), request.newPassword());
        recovery.markUsed();
        log.info("Password reset completed: user={}", recovery.getUser().getId());
    }

    private void startRecovery(User user) {
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
                LocalDateTime.now().plusMinutes(properties.getCodeExpiration())
        ));
        sendRecoveryCode(user, code);
    }

    private void revokeAndFlush(PasswordRecovery recovery) {
        recovery.revoke();
        passwordRecoveryRepository.flush();
    }

    private void sendRecoveryCode(User user, String code) {
        try {
            passwordRecoveryMailer.sendRecoveryCode(user.getProfile().getEmail(), code);
            log.info("Password recovery code sent: user={}", user.getId());
        } catch (MailException exception) {
            log.error("Error sending the password recovery code: user={}", user.getId(), exception);
        }
    }

    private boolean isWithinResendCooldown(PasswordRecovery recovery) {
        return recovery.getCreatedAt()
                .isAfter(LocalDateTime.now().minusSeconds(properties.getResendCooldown()));
    }

    private boolean isCodeUsable(PasswordRecovery recovery) {
        return !recovery.isCodeExpired() && recovery.getAttempts() < properties.getMaxAttempts();
    }

    private boolean isResetTokenUsable(PasswordRecovery recovery) {
        return recovery.isVerified() && !recovery.isResetTokenExpired();
    }

    private static String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
