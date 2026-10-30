package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, UUID> {

    List<QuestionCategory> findAllByOrderByOrderAsc();

    @Query("""
        SELECT DISTINCT c FROM QuestionCategory c
        LEFT JOIN FETCH c.questions q
        ORDER BY c.order ASC NULLS LAST, q.order ASC NULLS LAST
    """)
    List<QuestionCategory> findAllWithQuestionsOrdered();
}