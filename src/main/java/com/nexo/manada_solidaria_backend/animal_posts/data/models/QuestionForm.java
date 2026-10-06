package com.nexo.manada_solidaria_backend.animal_posts.data.models;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question_forms")
public class QuestionForm {

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    private String iconName;

    private String placeholder;

    @Column(name = "sort_order")
    private Integer order;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    @OneToMany(mappedBy = "questionForm", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<QuestionFormDetail> details = new ArrayList<>();

    @Id
    private UUID id = UUID.randomUUID();
}