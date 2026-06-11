package com.nexo.manada_solidaria_backend.animal_post.data.repositories;

import com.nexo.manada_solidaria_backend.animal_post.data.models.AnimalPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AnimalPostRepository extends JpaRepository<AnimalPost, UUID> {

    @Query("select p from AnimalPost p where type(p) = :type")
    Page<AnimalPost> findAllByType(@Param("type") Class<? extends AnimalPost> type, Pageable pageable);
}
