package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, UUID> {
    List<QuestionCategory> findAllByOrderByOrderAsc();
}