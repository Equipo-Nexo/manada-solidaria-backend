package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.common.utils.StatusHistoryUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LostPost extends AnimalPost<LostPostStatusHistory> {
    @Column(nullable = false, updatable = false)
    private boolean hasOwner;
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LostPostStatusHistory> statusHistory = new ArrayList<>(
            List.of(new LostPostStatusHistory(StatusLostPost.CREATED, this))
    );

    public LostPost(String title, String description, String imageUrl, String sharePostUrl, boolean hasOwner, User owner, Location location, Animal animal) {
        super(title, description, imageUrl, sharePostUrl, owner, animal, location);
        this.hasOwner = hasOwner;
    }

    @Override
    public LostPostStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(this.statusHistory);
    }
}
