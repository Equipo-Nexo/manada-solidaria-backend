package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AdoptionPostRepository extends JpaRepository<AdoptionPost, UUID> {
    
    @Query("select p from AdoptionPost p join p.statusHistory h where h.finishedAt is null and h.status = :status")
    Page<AdoptionPost> findAllByCurrentStatus(@Param("status") StatusAdoptionPost status, Pageable pageable);
}
