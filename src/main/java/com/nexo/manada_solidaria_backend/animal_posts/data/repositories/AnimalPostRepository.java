package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalAge;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AnimalPostRepository extends JpaRepository<AnimalPost, UUID> {

    @EntityGraph(attributePaths = {"animal", "location", "owner", "owner.profile"})
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
            AND (:animalType IS NULL OR p.animal.type = :animalType)
            AND (:animalSize IS NULL OR p.animal.size = :animalSize)
            AND (:animalGender IS NULL OR p.animal.gender = :animalGender)
            AND (:animalAge IS NULL OR p.animal.age = :animalAge)
            AND (:animalColor IS NULL OR p.animal.color = :animalColor)
            """)
    Page<AnimalPost<?, ?>> findAllFiltered(
            @Param("type") String type,
            @Param("status") String status,
            @Param("animalType") AnimalType animalType,
            @Param("animalSize") AnimalSize animalSize,
            @Param("animalGender") AnimalGender animalGender,
            @Param("animalAge") AnimalAge animalAge,
            @Param("animalColor") String animalColor,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT p FROM AnimalPost p
                    LEFT JOIN LostPostStatusHistory lost
                        ON lost.post.id = p.id AND lost.finishedAt IS NULL
                    LEFT JOIN AdoptionPostStatusHistory adoption
                        ON adoption.post.id = p.id AND adoption.finishedAt IS NULL
                    WHERE lost.status IN :happyLostStatuses
                       OR adoption.status IN :happyAdoptionStatuses
                    ORDER BY COALESCE(lost.createdAt, adoption.createdAt) DESC
                    """,
            countQuery = """
                    SELECT count(p) FROM AnimalPost p
                    WHERE EXISTS (SELECT 1 FROM LostPostStatusHistory h
                                  WHERE h.post.id = p.id AND h.finishedAt IS NULL
                                    AND h.status IN :happyLostStatuses)
                       OR EXISTS (SELECT 1 FROM AdoptionPostStatusHistory h
                                  WHERE h.post.id = p.id AND h.finishedAt IS NULL
                                    AND h.status IN :happyAdoptionStatuses)
                    """
    )
    Page<AnimalPost<?, ?>> findHappyCases(
            @Param("happyLostStatuses") Set<StatusLostPost> happyLostStatuses,
            @Param("happyAdoptionStatuses") Set<StatusAdoptionPost> happyAdoptionStatuses,
            Pageable pageable
    );

    List<AnimalPost<?, ?>> findAllByOwner(User user);
}
