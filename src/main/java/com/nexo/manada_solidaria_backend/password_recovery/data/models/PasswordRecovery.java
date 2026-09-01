package com.nexo.manada_solidaria_backend.password_recovery.data.models;

import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private String codeHash;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    private int attempts = 0;
    private LocalDateTime verifiedAt;
    @Column(unique = true)
    private String resetTokenHash;
    private LocalDateTime resetTokenExpiresAt;
    private LocalDateTime usedAt;

    public PasswordRecovery(User user, String codeHash, LocalDateTime expiresAt) {
        this.user = user;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isVerified() {
        return verifiedAt != null;
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

    public void markVerified(String resetTokenHash, LocalDateTime resetTokenExpiresAt) {
        this.verifiedAt = LocalDateTime.now();
        this.resetTokenHash = resetTokenHash;
        this.resetTokenExpiresAt = resetTokenExpiresAt;
    }

    public void markUsed() {
        this.usedAt = LocalDateTime.now();
    }
}
