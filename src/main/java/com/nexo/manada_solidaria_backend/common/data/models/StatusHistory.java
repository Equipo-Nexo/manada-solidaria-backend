package com.nexo.manada_solidaria_backend.common.data.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class StatusHistory<S extends Enum<S>> {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private S status;
    private LocalDateTime finishedAt = null;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();

    public StatusHistory(S status) {
        this.status = status;
    }
}
