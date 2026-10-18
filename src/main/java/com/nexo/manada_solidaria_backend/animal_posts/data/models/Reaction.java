package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.ReactionType;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Enumerated
    private ReactionType type;
    @ManyToOne
    @JoinColumn(updatable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "post_id", updatable = false)
    private AnimalPost post;
    @Id
    private final UUID id = UUID.randomUUID();
}
