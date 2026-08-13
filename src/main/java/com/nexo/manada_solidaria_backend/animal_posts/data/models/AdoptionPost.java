package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.common.utils.StatusHistoryUtils;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdoptionPost extends AnimalPost<AdoptionPostStatusHistory> {
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AdoptionPostStatusHistory> statusHistory = new ArrayList<>(
            List.of(new AdoptionPostStatusHistory(StatusAdoptionPost.CREATED, this))
    );

    public AdoptionPost(String name, String description, String imageUrl, String sharePostUrl, String phoneNumber, User owner, Animal animal, Location location, boolean inTransit) {
        super(name, description, imageUrl, sharePostUrl, phoneNumber, owner, animal, location);
        startSearching(inTransit);
    }
    private void startSearching(boolean inTransit) {
        getCurrentStatus().finish();
        StatusAdoptionPost next = inTransit
                ? StatusAdoptionPost.SEARCHING_ADOPT
                : StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT;
        this.statusHistory.add(new AdoptionPostStatusHistory(next, this));
    }

    @Override
    public AdoptionPostStatusHistory getCurrentStatus() {
        return StatusHistoryUtils.getCurrentStatus(this.statusHistory);
    }

    @Override
    public AnimalPostFilter getType() {
        return AnimalPostFilter.ADOPTION;
    }

    @Override
    public void transitionTo(String targetStatus) {
        StatusAdoptionPost target = EnumUtils.parseOrThrow(StatusAdoptionPost.class, targetStatus);
        AdoptionPostStatusHistory current = getCurrentStatus();

        if (!isTransitionAllowed(current.getStatus(), target)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede pasar de " + current.getStatus() + " a " + target
            );
        }

        current.finish();
        this.statusHistory.add(new AdoptionPostStatusHistory(target, this));
    }

    private boolean isTransitionAllowed(StatusAdoptionPost current, StatusAdoptionPost target) {
        return switch (current) {
            case SEARCHING_ADOPT, SEARCHING_ADOPT_AND_TRANSIT ->
                    target != StatusAdoptionPost.CREATED && target != current;
            case CREATED, ADOPTED -> false;
        };
    }
}
