package com.nexo.manada_solidaria_backend.animal_post.data.models;

import com.nexo.manada_solidaria_backend.animal_post.data.enums.StatusLostPost;
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
public class LostPostStatusHistory extends StatusHistory<StatusLostPost> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_post_id", nullable = false)
    private LostPost post;

    public LostPostStatusHistory(StatusLostPost status) {
        super(status);
    }
}
