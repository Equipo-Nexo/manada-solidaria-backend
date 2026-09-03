package com.nexo.manada_solidaria_backend.password_recovery.data.models;

import com.nexo.manada_solidaria_backend.password_recovery.data.enums.PasswordRecoveryStatus;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class PasswordRecovery {

    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne(optional = false)
    private User user;
    @Column(nullable = false)
    private String verificationCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PasswordRecoveryStatus status = PasswordRecoveryStatus.ACTIVE;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    private int attempts = 0;
    private LocalDateTime verifiedAt;
    @Column(unique = true)
    private String resetToken;
    private LocalDateTime resetTokenExpiresAt;
    private LocalDateTime usedAt;

    public PasswordRecovery(User user, String verificationCode, LocalDateTime expiresAt) {
        this.user = user;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }

    public boolean isVerified() {
        return status == PasswordRecoveryStatus.VERIFIED;
    }

    public boolean isCodeExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isResetTokenExpired() {
        return LocalDateTime.now().isAfter(resetTokenExpiresAt);
    }

    public void registerFailedAttempt() {
        this.attempts++;
    }

    public void revoke() {
        this.status = PasswordRecoveryStatus.REVOKED;
    }

    public void markVerified(String resetToken, LocalDateTime resetTokenExpiresAt) {
        this.status = PasswordRecoveryStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
        this.resetToken = resetToken;
        this.resetTokenExpiresAt = resetTokenExpiresAt;
    }

    public void markUsed() {
        this.status = PasswordRecoveryStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}
