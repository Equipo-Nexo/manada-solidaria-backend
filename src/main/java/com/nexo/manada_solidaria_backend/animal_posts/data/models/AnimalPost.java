package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AnimalPost<T extends StatusHistory> {
    private String title;
    private String description;
    private String imageUrl;
    private String sharePostUrl;
    private String phoneNumber;
    private LocalDateTime updatedAt = null;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(updatable = false)
    private Location location;
    @ManyToOne
    @JoinColumn(updatable = false)
    private User owner;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(updatable = false)
    private Animal animal;
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
    private UUID id = UUID.randomUUID();

    public AnimalPost(String title, String description, String imageUrl, String sharePostUrl, String phoneNumber, User owner, Animal animal, Location location) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sharePostUrl = sharePostUrl;
        this.phoneNumber = phoneNumber;
        this.owner = owner;
        this.animal = animal;
        this.location = location;
    }


    public void update(String title, String description, String imageUrl, String phoneNumber, BigDecimal reward) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
        updateReward(reward);
    }

    protected void updateReward(BigDecimal reward) {
    }

    public abstract T getCurrentStatus();
}
