package com.nexo.manada_solidaria_backend.campaigns.data.models;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Campaign {
    @Column(length = 50, nullable = false)
    private String title;
    private String description;
    private String imageUrl;
    private String shareCampaignUrl;
    private LocalDateTime updatedAt = null;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(optional = false)
    private Location location;
    @ManyToOne(optional = false)
    private User owner;
    @Id
    private final UUID id = UUID.randomUUID();

}
