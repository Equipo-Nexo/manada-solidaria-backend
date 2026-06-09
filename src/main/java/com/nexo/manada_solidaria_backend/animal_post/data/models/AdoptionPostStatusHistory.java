package com.nexo.manada_solidaria_backend.animal_post.data.models;

import com.nexo.manada_solidaria_backend.animal_post.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.common.data.models.StatusHistory;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@NoArgsConstructor
public class AdoptionPostStatusHistory extends StatusHistory<StatusAdoptionPost> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_post_id", nullable = false)
    private AdoptionPost post;

    public AdoptionPostStatusHistory(StatusAdoptionPost status) {
        super(status);
    }
}
