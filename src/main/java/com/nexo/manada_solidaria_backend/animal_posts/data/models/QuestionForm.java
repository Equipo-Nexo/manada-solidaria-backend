package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionForm {

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 1000)
    private String answer;
}