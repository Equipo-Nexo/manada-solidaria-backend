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
@Table(name = "question_categories")
public class QuestionCategory {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "sort_order")
    private Integer order;

    @Id
    private UUID id = UUID.randomUUID();
}