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
@Table(name = "question_form_details")
public class QuestionFormDetail {

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_form_id", nullable = false)
    private QuestionForm questionForm;

    @Id
    private UUID id = UUID.randomUUID();
}