package com.nexo.manada_solidaria_backend.notifications.models.data;

import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PushSubscription {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 2048)
    private String endpoint;

    @Column(name = "endpoint_hash", nullable = false, unique = true, columnDefinition = "BINARY(32)")
    private byte[] endpointHash;

    @Column(nullable = false, length = 512)
    private String p256dh;

    @Column(nullable = false, length = 512)
    private String auth;

    @Column
    private LocalDateTime updatedAt = null;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Id
    private UUID id = UUID.randomUUID();
}
