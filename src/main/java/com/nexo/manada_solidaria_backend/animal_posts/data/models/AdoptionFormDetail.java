package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "adoption_form_details")
public class AdoptionFormDetail {

    @Column(length = 1000)
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_form_id", nullable = false)
    private AdoptionForm adoptionForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_form_id", nullable = false)
    private QuestionForm questionForm;

    @Id
    private UUID id = UUID.randomUUID();

    public AdoptionFormDetail(String answer, AdoptionForm adoptionForm, QuestionForm questionForm) {
        this.answer = answer;
        this.adoptionForm = adoptionForm;
        this.questionForm = questionForm;
    }
}