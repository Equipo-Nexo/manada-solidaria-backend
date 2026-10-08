package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "adoption_forms")
public class AdoptionForm {

    @Column(length = 1000)
    private String description;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * User who submitted the adoption application.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    /**
     * Adoption post for which the user submitted the application.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_post_id", nullable = false)
    private AdoptionPost adoptionPost;

    @OneToMany(
            mappedBy = "adoptionForm",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AdoptionFormDetail> answers = new ArrayList<>();

    @Id
    private UUID id = UUID.randomUUID();

    public AdoptionForm(String description, PhoneNumber phoneNumber, User applicant, AdoptionPost adoptionPost) {
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.applicant = applicant;
        this.adoptionPost = adoptionPost;
    }

    public void addAnswer(AdoptionFormDetail detail) {
        this.answers.add(detail);
        detail.setAdoptionForm(this);
    }
}