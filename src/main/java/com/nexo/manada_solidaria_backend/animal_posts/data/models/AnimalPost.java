package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AnimalPost<STATUS extends Enum<STATUS>, HISTORY extends StatusHistory<STATUS>> {
    private static final int RECENT_DAYS = 7;
    private String name;
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

    public AnimalPost(String name, String description, String imageUrl, String sharePostUrl, String phoneNumber, User owner, Animal animal, Location location) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sharePostUrl = sharePostUrl;
        this.phoneNumber = phoneNumber;
        this.owner = owner;
        this.animal = animal;
        this.location = location;
    }


    public void update(UpdateAnimalPostRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.imageUrl = request.imageId();
        this.phoneNumber = request.phoneNumber();
        this.updatedAt = LocalDateTime.now();
        this.animal.update(request.animal());
        this.location.update(request.location());
    }

    public abstract HISTORY getCurrentStatus();
    
    public abstract AnimalPostFilter getType();

    public boolean isRecentlyResolved() {
        return getCurrentStatus().getCreatedAt().toLocalDate()
                .isAfter(LocalDate.now().minusDays(RECENT_DAYS));
    }

    public void transitionTo(String targetStatus) {
        STATUS target = EnumUtils.parseOrThrow(statusType(), targetStatus);
        HISTORY current = getCurrentStatus();

        if (!isTransitionAllowed(current.getStatus(), target)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede pasar de " + current.getStatus() + " a " + target
            );
        }

        current.finish();
        addStatus(target);
    }

    protected abstract Class<STATUS> statusType();

    protected abstract boolean isTransitionAllowed(STATUS current, STATUS target);

    protected abstract void addStatus(STATUS status);
}
