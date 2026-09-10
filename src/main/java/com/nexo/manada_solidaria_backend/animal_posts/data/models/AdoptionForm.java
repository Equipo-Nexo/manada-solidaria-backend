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

    @ElementCollection
    @CollectionTable(
            name = "adoption_form_questions",
            joinColumns = @JoinColumn(name = "adoption_form_id")
    )
    private List<QuestionForm> questions = new ArrayList<>();

    @Id
    private UUID id = UUID.randomUUID();

    public AdoptionForm(PhoneNumber phoneNumber, User applicant, AdoptionPost adoptionPost, List<QuestionForm> questions) {
        this.phoneNumber = phoneNumber;
        this.applicant = applicant;
        this.adoptionPost = adoptionPost;
        this.questions = questions;
    }
}