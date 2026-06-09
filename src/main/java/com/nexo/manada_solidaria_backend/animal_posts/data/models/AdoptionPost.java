package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AdoptionPost extends AnimalPost {
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AdoptionPostStatusHistory> statusHistory = new ArrayList<>();

    public AdoptionPost(String title, String description, String imageUrl, String sharePostUrl, User owner, Animal animal, Location location) {
        super(title, description, imageUrl, sharePostUrl, owner, animal, location);
    }
}
