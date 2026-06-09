package com.nexo.manada_solidaria_backend.animal_post.data.models;

import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
public class Comment {
    private final String message;
    private LocalDateTime updatedAt;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    private final User owner;
    @ManyToOne
    private final AnimalPost post;
    @Id
    private final UUID id = UUID.randomUUID();
}
