package com.nexo.manada_solidaria_backend.animal_post.data.models;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(force = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AnimalPost implements Persistable<UUID> {
    private String title;
    private String description;
    private String imageUrl;
    private String sharePostUrl;
    private LocalDateTime updatedAt = null;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(cascade = CascadeType.PERSIST)
    private final Location location;
    @ManyToOne
    private final User owner;
    @OneToOne(cascade = CascadeType.PERSIST)
    private final Animal animal;
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Reaction> reactions = new ArrayList<>();
    @Id
    private final UUID id = UUID.randomUUID();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Transient
    private boolean isNew = true;

    public AnimalPost(String title, String description, String imageUrl, String sharePostUrl, User owner, Animal animal, Location location) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sharePostUrl = sharePostUrl;
        this.owner = owner;
        this.animal = animal;
        this.location = location;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
