package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.ReactionType;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
public class Reaction {
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Enumerated
    private ReactionType type;
    @ManyToOne
    private final User owner;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private final AnimalPost post;
    @Id
    private final UUID id = UUID.randomUUID();
}
