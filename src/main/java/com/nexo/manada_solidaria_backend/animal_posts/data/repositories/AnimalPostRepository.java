package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AnimalPostRepository extends JpaRepository<AnimalPost, UUID> {

    @Query("""
            SELECT p FROM AnimalPost p
            WHERE (:type IS NULL
                OR (:type = 'LOST' AND TYPE(p) = LostPost AND TREAT(p AS LostPost).hasOwner = true)
                OR (:type = 'IN_STREET' AND TYPE(p) = LostPost AND TREAT(p AS LostPost).hasOwner = false)
                OR (:type = 'ADOPTION' AND TYPE(p) = AdoptionPost))
            AND (:status IS NULL
                OR EXISTS (SELECT 1 FROM LostPostStatusHistory h
                           WHERE h.post.id = p.id AND h.finishedAt IS NULL AND cast(h.status as string) = :status)
                OR EXISTS (SELECT 1 FROM AdoptionPostStatusHistory h
                           WHERE h.post.id = p.id AND h.finishedAt IS NULL AND cast(h.status as string) = :status))
            """)
    Page<AnimalPost<?, ?>> findAllFiltered(@Param("type") String type, @Param("status") String status, Pageable pageable);

    @Query("""
            SELECT count(p) FROM AnimalPost p
            WHERE p.owner = :owner
              AND (EXISTS (SELECT 1 FROM LostPostStatusHistory h
                           WHERE h.post.id = p.id AND h.finishedAt IS NULL
                             AND h.status IN :happyLostStatuses)
                OR EXISTS (SELECT 1 FROM AdoptionPostStatusHistory h
                           WHERE h.post.id = p.id AND h.finishedAt IS NULL
                             AND h.status IN :happyAdoptionStatuses))
            """)
    long countFinishedByOwner(
            @Param("owner") User owner,
            @Param("happyLostStatuses") Set<StatusLostPost> happyLostStatuses,
            @Param("happyAdoptionStatuses") Set<StatusAdoptionPost> happyAdoptionStatuses
    );

    long countByOwner(User owner);

    List<AnimalPost<?, ?>> findAllByOwner(User user);
}
