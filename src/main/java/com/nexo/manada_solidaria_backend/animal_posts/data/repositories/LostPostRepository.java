package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LostPostRepository extends JpaRepository<LostPost, UUID> {

    // Estado actual = la entrada del historial aún abierta (finishedAt null).
    @Query("select p from LostPost p join p.statusHistory h where h.finishedAt is null and h.status = :status")
    Page<LostPost> findAllByCurrentStatus(@Param("status") StatusLostPost status, Pageable pageable);
}
