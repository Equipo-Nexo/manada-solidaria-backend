package com.nexo.manada_solidaria_backend.common.data.models;

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
@Table(name = "report_post", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
})
public class ReportPost {

    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private User owner;

    @Id
    private final UUID id = UUID.randomUUID();

    public ReportPost(UUID postId, User owner) {
        this.postId = postId;
        this.owner = owner;
    }
}