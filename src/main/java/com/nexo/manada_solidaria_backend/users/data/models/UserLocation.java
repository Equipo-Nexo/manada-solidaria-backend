package com.nexo.manada_solidaria_backend.users.data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class UserLocation {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();

    public UserLocation(User user, Double latitude, Double longitude) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
