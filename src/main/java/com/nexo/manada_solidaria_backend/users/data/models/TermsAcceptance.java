package com.nexo.manada_solidaria_backend.users.data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class TermsAcceptance {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 20)
    private String termsVersion;
    @Column(nullable = false)
    private final LocalDateTime acceptedAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();

    public TermsAcceptance(User user, String termsVersion) {
        this.user = user;
        this.termsVersion = termsVersion;
    }
}
