package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class LostPost extends AnimalPost {
    @Column(nullable = false)
    private final boolean hasOwner;
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LostPostStatusHistory> statusHistory;

    public LostPost(String title, String description, String imageUrl, String sharePostUrl, boolean hasOwner, User owner, Location location, Animal animal) {
        super(title, description, imageUrl, sharePostUrl, owner, animal, location);
        this.hasOwner = hasOwner;
    }
}
