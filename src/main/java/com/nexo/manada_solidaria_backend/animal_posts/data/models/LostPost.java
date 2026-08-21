package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
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
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LostPost extends AnimalPost<StatusLostPost, LostPostStatusHistory> {

    public static final Set<StatusLostPost> HAPPY_STATUSES = Set.of(StatusLostPost.FOUND, StatusLostPost.RESCUED);
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

    public LostPost(String name, String description, String imageUrl, String sharePostUrl, PhoneNumber phoneNumber, boolean hasOwner, User owner, Location location, Animal animal, BigDecimal reward) {
        super(name, description, imageUrl, sharePostUrl, phoneNumber, owner, animal, location);
        this.hasOwner = hasOwner;
        this.reward = reward;
        startInitialStatus();
    }

    private void startInitialStatus() {
        getCurrentStatus().finish();
        StatusLostPost initial = hasOwner ? StatusLostPost.SEARCHING : StatusLostPost.TO_RESCUE;
        this.statusHistory.add(new LostPostStatusHistory(initial, this));
    }

    @Override
    public void update(UpdateAnimalPostRequest request) {
        super.update(request);
        this.reward = request.reward();
    }

    @Override
    public LostPostStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(this.statusHistory);
    }

    @Override
    public AnimalPostFilter getType() {
        return hasOwner ? AnimalPostFilter.LOST : AnimalPostFilter.IN_STREET;
    }

    @Override
    protected Class<StatusLostPost> statusType() {
        return StatusLostPost.class;
    }

    @Override
    protected boolean isTransitionAllowed(StatusLostPost current, StatusLostPost target) {
        return switch (current) {
            case SEARCHING -> target == StatusLostPost.FOUND;
            case TO_RESCUE -> target == StatusLostPost.RESCUED;
            case CREATED, FOUND, RESCUED -> false;
        };
    }

    @Override
    protected void addStatus(StatusLostPost status) {
        this.statusHistory.add(new LostPostStatusHistory(status, this));
    }
}
