package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AnimalPost {
    private String title;
    private String description;
    private String imageUrl;
    private String sharePostUrl;
    private LocalDateTime updatedAt = null;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    private final Location location;
    @ManyToOne
    private final User owner;
    @OneToOne
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

    public AnimalPost(String title, String description, String imageUrl, String sharePostUrl, User owner, Animal animal, Location location) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sharePostUrl = sharePostUrl;
        this.owner = owner;
        this.animal = animal;
        this.location = location;
    }
}
