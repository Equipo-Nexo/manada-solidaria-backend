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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LostPost extends AnimalPost<LostPostStatusHistory> {
    @Column(nullable = false, updatable = false)
    private boolean hasOwner;
    @Column(precision = 12, scale = 2)
    private BigDecimal reward;
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LostPostStatusHistory> statusHistory = new ArrayList<>(
            List.of(new LostPostStatusHistory(StatusLostPost.CREATED, this))
    );

    public LostPost(String title, String description, String imageUrl, String sharePostUrl, String phoneNumber, boolean hasOwner, User owner, Location location, Animal animal, BigDecimal reward) {
        super(title, description, imageUrl, sharePostUrl, phoneNumber, owner, animal, location);
        this.hasOwner = hasOwner;
        this.reward = reward;
    }

    @Override
    public LostPostStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(this.statusHistory);
    }
}
